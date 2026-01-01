package com.example.netapp.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.netapp.dto.TimeSlotDto;
import com.example.netapp.dto.requests.AppointmentRequest;
import com.example.netapp.entity.AppointmentEntity;
import com.example.netapp.entity.AppointmentStatus;
import com.example.netapp.entity.ServiceEntity;
import com.example.netapp.entity.ServiceWorkingHours;
import com.example.netapp.entity.UserEntity;
import com.example.netapp.exceptions.BadRequestException;
import com.example.netapp.exceptions.HttpException;
import com.example.netapp.exceptions.NotFoundException;
import com.example.netapp.exceptions.SchedulingConflictException;
import com.example.netapp.repository.AppointmentRepository;
import com.example.netapp.repository.ServiceRepository;
import com.example.netapp.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class SchedulingService {

	@Autowired
	private AppointmentRepository appointmentRepository;
	
	@Autowired 
	private UserRepository userRepo;


	@Autowired 
	private ServiceRepository serviceRepo;
	
	@Transactional
	public AppointmentEntity schedule(AppointmentRequest request) {

	    LocalDateTime start = request.startDateTime();
	    LocalDateTime end   = request.endDateTime();

	    if (!end.isAfter(start)) {
	        throw new HttpException(HttpStatus.BAD_REQUEST, "End must be after start");
	    }

	    ServiceEntity service = serviceRepo.findById(request.serviceId())
	        .orElseThrow(() -> new NotFoundException("Service not found"));

	    if (!Boolean.TRUE.equals(service.getIsActive())) {
	        throw new SchedulingConflictException("Service inactive");
	    }

	    // Check service working hours
	    boolean open = service.getWorkingHours().stream()
	        .anyMatch(h ->
	            h.getIsOpen()
	            && h.getDayOfWeek() == start.getDayOfWeek()
	            && !start.toLocalTime().isBefore(h.getOpenTime())
	            && !end.toLocalTime().isAfter(h.getCloseTime())
	        );

	    if (!open) {
	        throw new SchedulingConflictException("Service closed");
	    }

	    int buffer = service.getBufferMinutes() == null ? 0 : service.getBufferMinutes();

	    boolean collision = appointmentRepository.existsServiceCollision(
	        service,
	        start.minusMinutes(buffer),
	        end.plusMinutes(buffer)
	    );

	    if (collision) {
	        throw new SchedulingConflictException("Time slot unavailable");
	    }

	    UserEntity customer =
	        (UserEntity) SecurityContextHolder.getContext()
	            .getAuthentication()
	            .getPrincipal();

	    AppointmentEntity appointment = new AppointmentEntity();
	    appointment.setStartDateTime(start);
	    appointment.setEndDateTime(end);
	    appointment.setStatus(AppointmentStatus.PENDING);
	    appointment.setCustomer(customer);
	    appointment.setService(service);

	    return appointmentRepository.save(appointment);
	}
	
	
	public List<TimeSlotDto> getAvailability(
		    Long serviceId,
		    LocalDate date,
		    int durationMinutes
		) {

		    ServiceEntity service = serviceRepo.findById(serviceId)
		        .orElseThrow(() -> new NotFoundException("Service not found"));

		    ServiceWorkingHours hours = service.getWorkingHours().stream()
		        .filter(h -> h.getIsOpen() && h.getDayOfWeek() == date.getDayOfWeek())
		        .findFirst()
		        .orElse(null);

		    if (hours == null) return List.of();

		    LocalDateTime open  = date.atTime(hours.getOpenTime());
		    LocalDateTime close = date.atTime(hours.getCloseTime());

		    List<AppointmentEntity> appointments =
		        appointmentRepository.findAppointmentsForDay(service, open, close);

		    int buffer = service.getBufferMinutes() == null ? 0 : service.getBufferMinutes();
		    Duration duration = Duration.ofMinutes(durationMinutes);

		    List<TimeSlotDto> slots = new ArrayList<>();
		    LocalDateTime cursor = open;

		    for (AppointmentEntity a : appointments) {

		        LocalDateTime busyStart = a.getStartDateTime().minusMinutes(buffer);
		        LocalDateTime busyEnd   = a.getEndDateTime().plusMinutes(buffer);

		        if (Duration.between(cursor, busyStart).compareTo(duration) >= 0) {
		            slots.add(new TimeSlotDto(cursor, cursor.plus(duration)));
		        }

		        if (busyEnd.isAfter(cursor)) {
		            cursor = busyEnd;
		        }
		    }

		    if (Duration.between(cursor, close).compareTo(duration) >= 0) {
		        slots.add(new TimeSlotDto(cursor, cursor.plus(duration)));
		    }

		    return slots;
		}

	
	
	
	
	
}

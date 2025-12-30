package com.example.netapp.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.netapp.dto.requests.AppointmentRequest;
import com.example.netapp.entity.AppointmentEntity;
import com.example.netapp.entity.AppointmentStatus;
import com.example.netapp.entity.ServiceEntity;
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
	        throw new HttpException(HttpStatus.BAD_REQUEST,"End time must be after start time");
	    }

	    boolean collision =
	        appointmentRepository.existsAnyCollision(start, end);

	    if (collision) {
	    	throw new SchedulingConflictException("Time slot is already occupied");
	    }

	    AppointmentEntity appointment = new AppointmentEntity();
	    // this takes the user directly from the database and the token so all the fields are updated .
	    UserEntity customer = (UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    ServiceEntity service = serviceRepo.findById(request.serviceId()).orElse(null);
	    if(service == null) {
	    	throw new NotFoundException("customer id is not found");
	    }
	    
	    appointment.setStartDateTime(start);
	    appointment.setEndDateTime(end);
	    appointment.setStatus(AppointmentStatus.PENDING);
	    appointment.setCustomer(customer);
	    appointment.setService(service);

	    return appointmentRepository.save(appointment);
	}
}

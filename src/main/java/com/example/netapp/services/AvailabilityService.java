package com.example.netapp.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.netapp.dto.responses.TimeSlotResponse;
import com.example.netapp.entity.AppointmentEntity;
import com.example.netapp.entity.AppointmentStatus;
import com.example.netapp.entity.ServiceEntity;
import com.example.netapp.entity.WorkingSchedule;
import com.example.netapp.repository.AppointmentRepository;
import com.example.netapp.repository.WorkingScheduleRepository;

@Service 
public class AvailabilityService {

	@Autowired
	private WorkingScheduleRepository scheduleRepo;
	
	@Autowired
	private AppointmentRepository appointmentRepo;
	
	
	
	
	public List<TimeSlotResponse> getAvailableTimeSlots(ServiceEntity service, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // 1. Load working schedules for that day
        List<WorkingSchedule> schedules =
                scheduleRepo.findByServiceIdAndDayOfWeek(service.getServiceId(), dayOfWeek);

        // 2. Load booked appointments for that admin on that day
        LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        LocalDateTime dayEnd = dayStart.plusDays(1);

        List<AppointmentEntity> bookedAppointments =
                appointmentRepo.findByServiceIdAndStatusIn(
                		service.getServiceId(),
                		List.of(
                				AppointmentStatus.APPROVED,
                				AppointmentStatus.PENDING,
                				AppointmentStatus.COMPLETED)).stream()
                        .filter(a -> !a.getEndDateTime().isBefore(dayStart) && !a.getStartDateTime().isAfter(dayEnd))
                        .collect(Collectors.toList());
        Collections.sort(bookedAppointments, new Comparator<AppointmentEntity>() {
            @Override
            public int compare(AppointmentEntity a1, AppointmentEntity a2) {
                return a1.getStartDateTime().compareTo(a2.getStartDateTime());
            }
        });

        List<TimeSlotResponse> freeSlots = new ArrayList<>();

        // 3. Iterate through working schedules and compute free gaps
        for (WorkingSchedule ws : schedules) {
            LocalDateTime wsStart = LocalDateTime.of(date, ws.getStartTime());
            LocalDateTime wsEnd = LocalDateTime.of(date, ws.getEndTime());

            LocalDateTime cursor = wsStart;

            for (AppointmentEntity booked : bookedAppointments) {
                LocalDateTime bookedStart = booked.getStartDateTime();
                LocalDateTime bookedEnd = booked.getEndDateTime();

                // Skip if the booked appointment is completely before or after this working schedule
                if (bookedEnd.isBefore(wsStart) || bookedStart.isAfter(wsEnd)) continue;

                // Free slot before the booked appointment
                if (bookedStart.isAfter(cursor)) {
                    freeSlots.add(new TimeSlotResponse(cursor.toLocalTime(), bookedStart.toLocalTime()));
                }

                // Move cursor past the booked appointment
                if (bookedEnd.isAfter(cursor)) {
                    cursor = bookedEnd;
                }
            }

            // Free slot after the last booked appointment
            if (cursor.isBefore(wsEnd)) {
                freeSlots.add(new TimeSlotResponse(cursor.toLocalTime(), wsEnd.toLocalTime()));
            }
        }

        return freeSlots;
    }
}

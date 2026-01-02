package com.example.netapp.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.netapp.dto.TimeSlotDto;
import com.example.netapp.dto.requests.AppointmentRequest;
import com.example.netapp.entity.AppointmentEntity;
import com.example.netapp.entity.ServiceEntity;
import com.example.netapp.entity.UserEntity;
import com.example.netapp.exceptions.HttpException;
import com.example.netapp.repository.ServiceRepository;
import com.example.netapp.services.AppointmentService;
import com.example.netapp.services.AvailabilityService;
import com.example.netapp.services.UserServices;

@RestController 
@RequestMapping("/api/v1/schedual")
public class SchedulingController {


	@Autowired
	private AvailabilityService availabitlityService;
	@Autowired
	private ServiceRepository serviceRepo;
	@Autowired
	private AppointmentService appointmentService; 

	
	
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@PostMapping
	public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequest req) {
		UserEntity customer =
		        (UserEntity) SecurityContextHolder.getContext()
		            .getAuthentication()
		            .getPrincipal();
		ServiceEntity service =
				   serviceRepo.findById(req.serviceId())
				   .orElseThrow(() -> 
				   new HttpException(HttpStatus.NOT_FOUND , "the service with provided ID was not found"
						   ));
		AppointmentEntity appointment = 
				appointmentService.createAppointment(
						customer, 
						service, 
						req.startDateTime(), 
						req.sessionsBooked()
				);
		return ResponseEntity.ok(appointment);
	}
	
	@GetMapping("/{id}/availability")
	public ResponseEntity<?> availability(
	    @PathVariable("id") Long serviceId,
	    @RequestParam LocalDate date
	) {
		if(date == null) throw new HttpException(HttpStatus.BAD_REQUEST,"you need to provice a date");
	   ServiceEntity service =
			   serviceRepo.findById(serviceId)
			   .orElseThrow(() -> 
			   new HttpException(HttpStatus.NOT_FOUND , "the service with provided ID was not found"
					   ));
	   return ResponseEntity.ok(availabitlityService.getAvailableTimeSlots(service, date));
	}
}

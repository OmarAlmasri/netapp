package com.example.netapp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.netapp.dto.requests.AppointmentRequest;
import com.example.netapp.entity.AppointmentEntity;
import com.example.netapp.services.SchedulingService;

@RestController 
@RequestMapping("/api/v1/schedual")
public class SchedulingController {

	private SchedulingService engine;
	public SchedulingController(SchedulingService engine) {
		this.engine = engine;
	}
	
	
	@PreAuthorize("hasRole('CUSTOMER')")
	@PostMapping
	public ResponseEntity<?> createApplintment(@RequestBody AppointmentRequest req) {
		AppointmentEntity appointment = engine.schedule(req);
		return ResponseEntity.ok(appointment);
	}
}

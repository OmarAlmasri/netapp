package com.example.netapp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.netapp.dto.requests.CreateServiceRequest;
import com.example.netapp.entity.ServiceEntity;
import com.example.netapp.repository.ServiceRepository;
import com.example.netapp.services.WorkingScheduleService;

@RestController
@RequestMapping("/api/v1/service")
public class ServiceController {

	@Autowired 
	private ServiceRepository serviceRepo;

	@Autowired 
	private WorkingScheduleService workingService;

	@GetMapping
	public List<ServiceEntity> getAllServices(){
		List<ServiceEntity> res = serviceRepo.findAllByActiveTrue();
		return res;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<?> createService(
				@RequestBody CreateServiceRequest req
	){
		ServiceEntity service = new ServiceEntity(); 
		service.setServiceName(req.name());
		service.setDescription(req.description());
		service.setDurationMinutes(30);
		service.setPrice(req.price());
		
		ServiceEntity saved = serviceRepo.save(service);
		workingService.createDefaultIfMissing(saved);
		
		return ResponseEntity.ok(saved);
	}
}

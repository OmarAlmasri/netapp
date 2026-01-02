package com.example.netapp.controllers;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.netapp.repository.WorkingScheduleRepository;
import com.example.netapp.services.WorkingScheduleService;
import com.example.netapp.dto.requests.WorkingScheduleUpdateRequest;
import com.example.netapp.entity.*;

@RestController
@RequestMapping("/api/v1/working-schedules")
public class WorkingScheduleController {

	@Autowired
	private WorkingScheduleRepository scheduleRepo;
	@Autowired
	private WorkingScheduleService scheduleService;

	@GetMapping("/service/{serviceId}")
	public ResponseEntity<List<WorkingSchedule>> getWorkingScheduleForService(
				@PathVariable Long serviceId, 
				@RequestParam DayOfWeek dayOfWeek
			){
		List<WorkingSchedule> schedules = 
				scheduleRepo.findByServiceIdAndDayOfWeek(serviceId, dayOfWeek);
		return ResponseEntity.ok(schedules);
	}
	
	@PutMapping("/update/{serviceId}")
    public ResponseEntity<String> updateSchedule(
            @PathVariable Long serviceId,
            @RequestBody List<WorkingScheduleUpdateRequest> updates) {

        scheduleService.updateSchedule(serviceId, updates);
        return ResponseEntity.ok("Working schedule updated successfully");
    }
	
}

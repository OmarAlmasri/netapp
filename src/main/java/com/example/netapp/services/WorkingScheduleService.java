package com.example.netapp.services;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.netapp.dto.requests.WorkingScheduleUpdateRequest;
import com.example.netapp.entity.ServiceEntity;
import com.example.netapp.entity.WorkingSchedule;
import com.example.netapp.exceptions.NotFoundException;
import com.example.netapp.repository.ServiceRepository;
import com.example.netapp.repository.WorkingScheduleRepository;

import jakarta.transaction.Transactional;

@Service
public class WorkingScheduleService {

	@Autowired
	private WorkingScheduleRepository repo;
    @Autowired
    private ServiceRepository serviceRepository;

	public void createDefaultIfMissing(ServiceEntity service) {
		for(DayOfWeek day : List.of(
					DayOfWeek.SUNDAY,
					DayOfWeek.MONDAY,
					DayOfWeek.TUESDAY,
					DayOfWeek.WEDNESDAY,
					DayOfWeek.THURSDAY
				)) {
			WorkingSchedule ws = new WorkingSchedule();
			ws.setService(service);
			ws.setDayOfWeek(day);
			ws.setStartTime(LocalTime.of(9, 0));
			ws.setEndTime(LocalTime.of(17, 0));
			repo.save(ws);
		}
	}
	
    @Transactional
	public void updateSchedule(Long serviceId, List<WorkingScheduleUpdateRequest> updates) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new NotFoundException("Service not found"));

        for (WorkingScheduleUpdateRequest req : updates) {
            List<WorkingSchedule> schedules = repo.findByServiceIdAndDayOfWeek(serviceId, req.getDayOfWeek());
            WorkingSchedule ws;
            if (schedules.isEmpty()) {
                ws = new WorkingSchedule();
                ws.setService(service);
                ws.setDayOfWeek(req.getDayOfWeek());
            } else {
                ws = schedules.get(0); // assuming only one schedule per day per service
            }

            ws.setStartTime(req.getStartTime());
            ws.setEndTime(req.getEndTime());
            ws.setValidFrom(req.getValidFrom());
            ws.setValidTo(req.getValidTo());

            repo.save(ws);
        }
    }
}

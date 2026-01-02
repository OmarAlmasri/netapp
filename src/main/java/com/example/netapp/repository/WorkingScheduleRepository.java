package com.example.netapp.repository;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.netapp.entity.WorkingSchedule;

@Repository
public interface WorkingScheduleRepository extends JpaRepository<WorkingSchedule , Long>{

	List<WorkingSchedule> findByServiceIdAndDayOfWeek(Long ServiceId,DayOfWeek dayOfWeek);
}

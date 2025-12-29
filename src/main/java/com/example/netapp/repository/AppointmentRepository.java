package com.example.netapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.netapp.entity.AppointmentEntity;
import java.time.LocalDateTime;


public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findByCustomer_UserId(Long userId);

    // Given a start and end date, find all appointments between those dates
    List<AppointmentEntity> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
}

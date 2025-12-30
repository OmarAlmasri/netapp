package com.example.netapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.netapp.entity.AppointmentEntity;
import java.time.LocalDateTime;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findByCustomer_UserId(Long userId);

    List<AppointmentEntity> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);

    /*
     * Global collision check: returns true if any appointment overlaps
     * JPQL compatible with H2, MySQL, PostgreSQL
     */
    @Query("""
                SELECT COUNT(a) > 0
                FROM AppointmentEntity a
                WHERE a.status <> 'CANCELLED'
                  AND a.startDateTime < :end
                  AND a.endDateTime > :start
            """)
    boolean existsAnyCollision(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /*
     * fetch all appointments that collide with a given interval
     * Useful if you want to return conflicting appointments instead of just boolean
     */
    @Query("""
                SELECT a
                FROM AppointmentEntity a
                WHERE a.status <> 'CANCELLED'
                  AND a.startDateTime < :end
                  AND a.endDateTime > :start
            """)
    List<AppointmentEntity> findCollidingAppointments(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}

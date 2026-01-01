package com.example.netapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.netapp.entity.NotificationEntity;
import com.example.netapp.entity.UserEntity;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    List<NotificationEntity> findByUserOrderBySentAtDesc(UserEntity user);

    List<NotificationEntity> findByUserAndIsReadFalseOrderBySentAtDesc(UserEntity user);

    Long countByUserAndIsReadFalse(UserEntity user);
}

package com.example.netapp.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.netapp.entity.ServiceEntity;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findAllByActiveTrue();
}

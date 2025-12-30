package com.example.netapp.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

@Entity
@Data
@Table(name = "service")
public class ServiceEntity {
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    private String serviceName;

    @Column(columnDefinition = "TEXT")
    private String description;

    // this price means the price per hour . 
    private Double price;
    private Boolean isActive = true;

    // Minutes between appointments
    private Integer bufferMinutes = 0;

    @ElementCollection
    @CollectionTable(
        name = "service_working_hours",
        joinColumns = @JoinColumn(name = "service_id")
    )
    private List<ServiceWorkingHours> workingHours;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Integer getBufferMinutes() {
		return bufferMinutes;
	}

	public void setBufferMinutes(Integer bufferMinutes) {
		this.bufferMinutes = bufferMinutes;
	}

	public List<ServiceWorkingHours> getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(List<ServiceWorkingHours> workingHours) {
		this.workingHours = workingHours;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(); }
}

package com.example.netapp.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.netapp.entity.AppointmentEntity;
import com.example.netapp.entity.ServiceEntity;
import com.example.netapp.exceptions.HttpException;

@Service
public class PricingService {

    public BigDecimal calculateAppointmentCost(
        AppointmentEntity appointment
    ) {

        ServiceEntity service = appointment.getService();

        if (service == null || service.getPrice() == null) {
            throw new HttpException(HttpStatus.valueOf(500),"Service pricing not configured");
        }

        long minutes = Duration.between(
            appointment.getStartDateTime(),
            appointment.getEndDateTime()
        ).toMinutes();

        if (minutes <= 0) {
            throw new HttpException(HttpStatus.BAD_REQUEST,"Invalid appointment duration");
        }

        BigDecimal pricePerMinute = BigDecimal.valueOf(service.getPrice());

        return pricePerMinute
            .multiply(BigDecimal.valueOf(minutes))
            .setScale(2, RoundingMode.UP);
    }
    
    
    public BigDecimal calculateCostPreview(
    	    ServiceEntity service,
    	    LocalDateTime start,
    	    LocalDateTime end
    	) {
    	    long minutes = Duration.between(start, end).toMinutes();

    	    if (minutes <= 0) {
    	    	throw new HttpException(HttpStatus.BAD_REQUEST,"Invalid appointment duration");
    	    }

    	    return BigDecimal.valueOf(service.getPrice())
    	        .multiply(BigDecimal.valueOf(minutes))
    	        .setScale(2, RoundingMode.UP);
    	}
}
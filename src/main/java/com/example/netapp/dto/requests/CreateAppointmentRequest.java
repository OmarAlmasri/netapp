package com.example.netapp.dto.requests;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(
		Long userId , 
		Long serviceId, 
		LocalDateTime start, 
		int sessionsBooked) {

}

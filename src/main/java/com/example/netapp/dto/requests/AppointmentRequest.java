package com.example.netapp.dto.requests;

import java.time.LocalDateTime;

public record AppointmentRequest(
		LocalDateTime startDateTime,
		int sessionsBooked,
		Long serviceId
		) {}

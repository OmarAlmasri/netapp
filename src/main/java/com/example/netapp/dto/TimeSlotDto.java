package com.example.netapp.dto;

import java.time.LocalDateTime;

public record TimeSlotDto(
	    LocalDateTime start,
	    LocalDateTime end
	) {}

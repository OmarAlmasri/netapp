package com.example.netapp.dto.requests;

public record CreateServiceRequest(
			String name, 
			String description, 
			int durationMinutes, // this should be 30 minutes but to keep room for updates we did this .
			double price
		) {}

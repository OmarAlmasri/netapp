package com.example.netapp.dto.responses;

import java.time.LocalTime;

public record TimeSlotResponse(
        LocalTime startTime,
        LocalTime endTime
) {}
package com.example.netapp.exceptions;

import org.springframework.http.HttpStatus;

public class SchedulingConflictException extends HttpException {

    public SchedulingConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
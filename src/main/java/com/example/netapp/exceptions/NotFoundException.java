package com.example.netapp.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends HttpException{
	public NotFoundException(String message) {
		super(HttpStatus.NOT_FOUND,message);
	}

	// to set a default message for the exception.
	public NotFoundException() {
		this("element not found");
	}
}

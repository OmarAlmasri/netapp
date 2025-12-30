package com.example.netapp.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends HttpException{

	public BadRequestException(String message) {
		super(HttpStatus.BAD_REQUEST ,message);
	}

	// to set a default message for the exception.
	public BadRequestException() {
		this("bad request");
	}
}

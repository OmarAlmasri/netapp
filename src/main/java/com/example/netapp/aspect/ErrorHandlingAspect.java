package com.example.netapp.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.netapp.dto.responses.ErrorResponse;
import com.example.netapp.exceptions.HttpException;

@Aspect
@Component
@Order(1)
public class ErrorHandlingAspect {

    @Around("execution(* com.example.netapp.controllers..*(..))")
    public Object handleErrors(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        }catch (HttpException ex) {
            return ResponseEntity
                    .status(ex.getStatus())
                    .body(new ErrorResponse(ex.getMessage()));
        }
    }
}
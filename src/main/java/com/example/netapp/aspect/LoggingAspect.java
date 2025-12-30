package com.example.netapp.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
public class LoggingAspect {
	private static final Logger log =
            LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut(
        "within(@org.springframework.web.bind.annotation.RestController *) || " +
        "within(@org.springframework.stereotype.Service *)"  
    )
    public void applicationLayer() {
    }
    
    @Around("applicationLayer()")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long durationMs = System.currentTimeMillis() - startTime;

            log.info(
                "method={} durationMs={}",
                joinPoint.getSignature().toShortString(),
                durationMs
            );

            return result;
        } catch (Exception ex) {
            log.error(
                "method={} failed",
                joinPoint.getSignature().toShortString()
                
            );
            throw ex;
        }
    }
}
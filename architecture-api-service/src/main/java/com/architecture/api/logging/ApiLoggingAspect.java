package com.architecture.api.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
public class ApiLoggingAspect {

    @Pointcut("within(com.architecture.api.controller..*)")
    public void restPointCut() {

    }

    @Around("restPointCut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Entering into {}.{} with argument[s] = {} ", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            log.info("Exiting from {}.{} with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), result);
            return result;
        } catch (Exception ex) {
            log.error("Error in {}.{}, exception = {} ", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), ex.getMessage());
            throw ex;
        }
    }

}
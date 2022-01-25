package com.junny.aspect.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class LoggingAspect {
    private double processTime;

    @Before(value = "bean(simpleEventService)")
    public Object beforeLogging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("=====BEFORE LOGGING : ");
        log.info("CLASS NAME     : "+proceedingJoinPoint.getSignature().getDeclaringTypeName());
        log.info("METHOD NAME     : "+proceedingJoinPoint.toLongString());
        log.info("=====BEFORE LOGGING : ");

        return proceedingJoinPoint.proceed();
    }

    private Object mainProcess(ProceedingJoinPoint pjp) throws Throwable {
        long startAt = System.currentTimeMillis();
        Object result = pjp.proceed();
        long endAt = System.currentTimeMillis();
        this.processTime = (endAt - startAt) / 1000.0;

        return result;
    }




}

package com.junny.aspect.common;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

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

    @Around("within(com.junny.common.*))")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result;
        LoggingModel loggingModel = loggingInitData(proceedingJoinPoint);
        String className = loggingModel.getClassName();
        String methodName = loggingModel.getMethodName();
        log.info(">>>>>> Logging start");
        log.info("URI          : {}", loggingModel.getUri());
        log.info("IP           : {}", loggingModel.getIp());
        log.info("PARAMS       : {}", loggingModel.getParams());
        log.info(">> {}.{}() process start -----", className, methodName);
        result = mainProcess(proceedingJoinPoint);
        log.info("<< {}.{}() process end   -----", className, methodName);
        log.info("PROCESS TIME : ({}s)", processTime);
        log.info("RESULT       : {}", result);
        log.info("<<<<<< Logging end");

        return result;
    }

    private Object mainProcess(ProceedingJoinPoint pjp) throws Throwable {
        long startAt = System.currentTimeMillis();
        Object result = pjp.proceed();
        long endAt = System.currentTimeMillis();
        this.processTime = (endAt - startAt) / 1000.0;

        return result;
    }
    private LoggingModel loggingInitData(ProceedingJoinPoint pjp) {
        String params = "X";
        String uri = "X";
        String ip = "X";
        RequestAttributes requestAttributes = RequestContextHolder
                .getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();
            Map<String, String[]> paramMap = request.getParameterMap();
            if (!paramMap.isEmpty()) {
                params = " [" + paramMapToString(paramMap) + "]";
            }
            uri = request.getRequestURI();

            ip = ofNullable(request.getHeader("X-FORWARDED-FOR"))
                    .orElse(request.getRemoteAddr());
        }
        String className = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();

        return LoggingModel.builder()
                .params(params)
                .uri(uri)
                .ip(ip)
                .className(className)
                .methodName(methodName)
                .build();
    }

    private String paramMapToString(Map<String, String[]> paramMap) {
        return paramMap.entrySet().stream()
                .map(entry -> String.format("%s -> (%s)",
                        entry.getKey(), Joiner.on(",").join(entry.getValue())))
                .collect(Collectors.joining(", "));
    }



}

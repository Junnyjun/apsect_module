package com.junny.aspect.common;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoggingModel {
    private String params;
    private String uri;
    private String ip;
    private String className;
    private String methodName;

    @Builder
    public LoggingModel(String params, String uri, String ip, String className, String methodName) {
        this.params = params;
        this.uri = uri;
        this.ip = ip;
        this.className = className;
        this.methodName = methodName;
    }
}


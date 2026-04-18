package com.mdrsolutions.thymeleaf.thymeleafsupport.spring;

public class ControllerMappingInfo {
    public final String url;
    public final String method; // "GET", "POST", etc.
    public final String controllerName;
    public final String methodName;
    public ControllerMappingInfo(String url, String httpMethod, String controllerName, String methodName) {
        this.url = url;
        this.method = httpMethod;
        this.controllerName = controllerName;
        this.methodName = methodName;
    }
}

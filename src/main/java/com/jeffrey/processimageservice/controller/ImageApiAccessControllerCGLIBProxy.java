package com.jeffrey.processimageservice.controller;

import com.jeffrey.processimageservice.entities.RequestParams;
import com.jeffrey.processimageservice.entities.RequestParamsWrapper;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.service.ProcessService;

import javax.servlet.http.HttpServletRequest;

public interface ImageApiAccessControllerCGLIBProxy {
    GenericResponse processSingleFile(RequestParams requestParams, HttpServletRequest request) throws InterruptedException;

    GenericResponse process(RequestParamsWrapper requestParamsWrapper, ProcessService processService);
}

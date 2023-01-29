package com.jeffrey.processimageservice.controller;

import com.jeffrey.processimageservice.aop.annotation.Report;
import com.jeffrey.processimageservice.entities.RequestParamsWrapper;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.service.ProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Controller
@Slf4j
@RequestMapping("/access")
public class ImageApiAccessController {

    private final ProcessService processService;
    private final ThreadLocal<RequestParamsWrapper> requestParamsWrapperThreadLocal;


    @Autowired
    public ImageApiAccessController(ProcessService processService, ThreadLocal<RequestParamsWrapper> requestParamsWrapperThreadLocal) {
        this.processService = processService;
        this.requestParamsWrapperThreadLocal = requestParamsWrapperThreadLocal;
    }

    @GetMapping()
    public String jumpDemoPage() {
        return "upload.html";
    }

    @PostMapping(produces = "application/json;charset=UTF-8")
    @ResponseBody
    @Report
    public GenericResponse processSingleFile() {

        RequestParamsWrapper requestParamsWrapper = requestParamsWrapperThreadLocal.get();

        return processService.taskAllocation(requestParamsWrapper, false, null, null);
    }
}

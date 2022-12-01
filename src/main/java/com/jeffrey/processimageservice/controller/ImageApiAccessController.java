package com.jeffrey.processimageservice.controller;

import com.jeffrey.processimageservice.aop.CheckAccountCallableCountAOP;
import com.jeffrey.processimageservice.aop.RecordUserUsedCountAOP;
import com.jeffrey.processimageservice.entities.response.Data;
import com.jeffrey.processimageservice.entities.RequestParams;
import com.jeffrey.processimageservice.entities.RequestParamsWrapper;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.service.ProcessService;
import com.jeffrey.processimageservice.service.UploadService;
import com.jeffrey.processimageservice.utils.GetRequestAddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Controller
@Slf4j
public class ImageApiAccessController {

    private final UploadService uploadService;
    private final ProcessService processService;

    @Autowired
    public ImageApiAccessController(UploadService uploadService, ProcessService processService) {
        this.uploadService = uploadService;
        this.processService = processService;
    }

    @GetMapping("/")
    public String jumpDemoPage(){
        return "/upload.html";
    }

    @PostMapping(value = "/single-upload", produces = "application/json;charset=UTF-8")
    @CheckAccountCallableCountAOP
    @ResponseBody
    public GenericResponse processSingleFile(RequestParams requestParams, HttpServletRequest request) throws InterruptedException {

        log.info("API Access");

        RequestParamsWrapper requestParamsWrapper = uploadService.argumentsOverwrite(requestParams);

        return this.processService.taskAllocation(requestParamsWrapper, false, null);
    }

    @PostMapping("/callback")
    @ResponseBody
    public void callBackTest(@RequestBody String body, @RequestHeader Map<String, Object> headers) {
        System.out.println(body);
    }

}

package com.jeffrey.processimageservice.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.jeffrey.processimageservice.aop.annotation.demo.CacheAspect;
import com.jeffrey.processimageservice.aop.annotation.demo.InitPublicAccountUserAspect;
import com.jeffrey.processimageservice.aop.annotation.demo.UpdatePublicAccountUserData;
import com.jeffrey.processimageservice.aop.annotation.generic.Report;
import com.jeffrey.processimageservice.entities.DemoRequestParams;
import com.jeffrey.processimageservice.entities.RequestParamsWrapper;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.exception.exception.clitent.AccountException;
import com.jeffrey.processimageservice.service.ProcessService;
import com.jeffrey.processimageservice.service.PublicAccountService;
import com.jeffrey.processimageservice.strategy.StrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author jeffrey
 * @since JDK 1.8
 */
@Slf4j
@Controller
@RequestMapping("/access")
public class ApiAccessController {

    private final ProcessService processService;
    private final PublicAccountService publicAccountService;
    private final ThreadLocal<RequestParamsWrapper> requestParamsWrapperThreadLocal;


    @Autowired
    public ApiAccessController(ProcessService processService, PublicAccountService publicAccountService, ThreadLocal<RequestParamsWrapper> requestParamsWrapperThreadLocal) {
        this.processService = processService;
        this.publicAccountService = publicAccountService;
        this.requestParamsWrapperThreadLocal = requestParamsWrapperThreadLocal;
    }

    @GetMapping
    public String jumpDemoPage() {
        return "welcome";
    }

    @GetMapping("/upload")
    public ModelAndView jumpUploadPage(){
        ModelAndView mav = new ModelAndView("upload");
        if (StpUtil.isLogin()) {
            mav.addObject("appId", StpUtil.getSession().get("appId"));
            mav.addObject("appSecret", StpUtil.getSession().get("appSecret"));
            mav.addObject("isLogin", true);
        }else{
            mav.addObject("appId", "");
            mav.addObject("appSecret", "");
            mav.addObject("isLogin", false);
        }
        return mav;
    }

    @GetMapping("/demo")
    public String jumpRectanglePage() {
        return "rectangle";
    }

    @PostMapping(value = "/demo-preview", produces = "application/json;charset=UTF-8")
    @InitPublicAccountUserAspect
    @UpdatePublicAccountUserData
    @CacheAspect
    @ResponseBody
    @Report
    public GenericResponse processPublicAccountPreviewFile(DemoRequestParams demoRequestParams) {

        if (!StrategyFactory.publicAccountExperienceAccessParamsIsOk(demoRequestParams)) {
            throw new AccountException("非法参数");
        }

        if (!publicAccountService.isPublicAccountUser(demoRequestParams.getOpenid())) {
            throw new AccountException("请先关注公众号");
        }

        return processService.simpleDemoProcess(demoRequestParams.getFile(), demoRequestParams.getRect());
    }

    @PostMapping(produces = "application/json;charset=UTF-8")
    @ResponseBody
    @Report
    public GenericResponse processApiSingleFile() {

        RequestParamsWrapper requestParamsWrapper = requestParamsWrapperThreadLocal.get();

        return processService.taskAllocation(requestParamsWrapper, false, null, null);
    }
}

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
import org.springframework.aop.framework.AopContext;
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
public class ImageApiAccessController implements ImageApiAccessControllerCGLIBProxy {

    private final UploadService uploadService;
    private final ProcessService processService;

    @Autowired
    public ImageApiAccessController(UploadService uploadService, ProcessService processService) {
        this.uploadService = uploadService;
        this.processService = processService;
    }

    @GetMapping("/")
    public String jumpDemoPage() {
        return "/upload.html";
    }

    @PostMapping(value = "/single-upload", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @Override
    public GenericResponse processSingleFile(RequestParams requestParams, HttpServletRequest request) throws InterruptedException {

        log.info("API Access");

        RequestParamsWrapper requestParamsWrapper = uploadService.argumentsOverwrite(requestParams);

        /*
            之所以通过 CGLIB 实现接口动态代理的方式是因为考虑到参数校验过程中出现异常仍然会扣除账户使用次数（
                参数校验前切入了 @CheckAccountCallableCountAOP，@CheckAccountCallableCountAOP 用于校验账户可使用次数以及预扣除账户使用次数
                如参数校验过程中出现异常，导致 AOP 切入并没有执行到 @RecordUserUsedCountAOP，@RecordUserUsedCountAOP 是判断本次调用是否出现异常，
                如出现异常则回滚次数）就抛出了参数验证异常，最终导致账户次数只增不减的 BUG

            解决的方法有多种
                1、所以将 @CheckAccountCallableCountAOP 单独标注到同类的方法中，并通过 AopContext.currentProxy(); 的方式调用标注方法，
                2、通过注入 ApplicationContext 调用 getBean() 获取当前类
                3、通过 @Autowired 导入当前类
                4、添加拦截器，在拦截器中校验并包装参数到 ThreadLocal<RequestParamsWrapper> 中
         */
        ImageApiAccessController imageApiAccessControllerProxy = (ImageApiAccessController) AopContext.currentProxy();
        System.out.println(imageApiAccessControllerProxy.getClass().getTypeName());

        return imageApiAccessControllerProxy.process(requestParamsWrapper, processService);
    }

    @CheckAccountCallableCountAOP
    @Override
    public GenericResponse process(RequestParamsWrapper requestParamsWrapper, ProcessService processService) {
        return processService.taskAllocation(requestParamsWrapper, false, null);
    }

}

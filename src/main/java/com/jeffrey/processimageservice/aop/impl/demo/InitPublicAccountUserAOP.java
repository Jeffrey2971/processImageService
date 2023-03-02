package com.jeffrey.processimageservice.aop.impl.demo;

import com.jeffrey.processimageservice.entities.DemoRequestParams;
import com.jeffrey.processimageservice.exception.exception.clitent.PublicAccountExperienceAccessException;
import com.jeffrey.processimageservice.service.PublicAccountService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Aspect
@Order(100)
@Component
@Slf4j
public class InitPublicAccountUserAOP {

    private final PublicAccountService publicAccountService;

    @Autowired
    public InitPublicAccountUserAOP(PublicAccountService publicAccountService) {
        this.publicAccountService = publicAccountService;
    }

    @Pointcut("@annotation(com.jeffrey.processimageservice.aop.annotation.demo.InitPublicAccountUserAspect)")
    private void pointCut() {
    }

    @Before(value = "pointCut()")
    public void check(JoinPoint jp) {
        DemoRequestParams demoRequestParams = ((DemoRequestParams) jp.getArgs()[0]);
        // args[2] -> String openid
        String openid = demoRequestParams.getOpenid();

        if (!StringUtils.hasText(openid)) {
            throw new PublicAccountExperienceAccessException("非法参数");
        }

        if (StringUtils.hasText(openid) && publicAccountService.isFirstAccess(openid)) {
            publicAccountService.initializationPublicUser(openid);
        }
    }
}

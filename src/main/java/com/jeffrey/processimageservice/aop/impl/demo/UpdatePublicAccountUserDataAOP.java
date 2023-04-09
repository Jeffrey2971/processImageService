package com.jeffrey.processimageservice.aop.impl.demo;

import com.jeffrey.processimageservice.entities.DemoRequestParams;
import com.jeffrey.processimageservice.entities.UpdatePublicAccountParams;
import com.jeffrey.processimageservice.enums.AccountStatus;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.exception.exception.clitent.AccountException;
import com.jeffrey.processimageservice.service.PublicAccountService;
import com.jeffrey.processimageservice.strategy.StrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Aspect
@Slf4j
public class UpdatePublicAccountUserDataAOP {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");

    private final PublicAccountService publicAccountService;

    @Autowired
    public UpdatePublicAccountUserDataAOP(PublicAccountService publicAccountService) {
        this.publicAccountService = publicAccountService;
    }

    @Pointcut("@annotation(com.jeffrey.processimageservice.aop.annotation.demo.UpdatePublicAccountUserData)")
    private void pointCut() {
    }

    @Around(value = "pointCut()")
    @Transactional
    public Object checkAccount(ProceedingJoinPoint pjp) throws Throwable {

        DemoRequestParams demoRequestParams = (DemoRequestParams) pjp.getArgs()[0];
        String openid = demoRequestParams.getOpenid();

        UpdatePublicAccountParams updatePublicAccountParams = publicAccountService.selectUserInfoByOpenid(openid);

        if (updatePublicAccountParams.getCanUse() <= AccountStatus.ZERO_USED.getValue()) {
            throw new AccountException("账户可用次数不足");
        }

        // 执行目标方法
        Object res = pjp.proceed();

        GenericResponse genericResponse = (GenericResponse) res;

        if (!StrategyFactory.shouldNotModifyCounts(genericResponse)) {
            updatePublicAccountParams.setOpenid(openid);
            updatePublicAccountParams.setCanUse(updatePublicAccountParams.getCanUse() - 1);
            updatePublicAccountParams.setAllUse(updatePublicAccountParams.getAllUse() + 1);
            updatePublicAccountParams.setLastModifyTime(SDF.format(new Date()));
            publicAccountService.updatePublicUserAccount(updatePublicAccountParams);
        }

        return genericResponse;
    }
}

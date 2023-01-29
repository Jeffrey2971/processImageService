package com.jeffrey.processimageservice.aop.impl;


import com.jeffrey.processimageservice.entities.ProcessStatus;
import com.jeffrey.processimageservice.entities.enums.ProcessStatusEnum;
import com.jeffrey.processimageservice.entities.enums.ResponseStatus;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.service.ProcessStatusService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Aspect
@Component
@Slf4j
public class ReportAop {

    private final ProcessStatusService processStatusService;
    private final ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Autowired
    public ReportAop(ProcessStatusService processStatusService, ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal) {
        this.processStatusService = processStatusService;
        this.encryptedInfoThreadLocal = encryptedInfoThreadLocal;
    }

    @Pointcut(value = "@annotation(com.jeffrey.processimageservice.aop.annotation.Report)")
    public void pointCut() {
    }

    @AfterThrowing(value = "pointCut()", throwing = "ex")
    public void reportException(JoinPoint jp, Exception ex) {
        EncryptedInfo encryptedInfo = encryptedInfoThreadLocal.get();
        Integer id = encryptedInfo.getId();

        reports(
                ProcessStatusEnum.NO,
                ex.getMessage(),

                SDF.format(new Date(System.currentTimeMillis())),
                id
        );
    }

    @Around(value = "pointCut()")
    public Object report(ProceedingJoinPoint pjp) throws Throwable {
        log.info("ReportAop");
        EncryptedInfo encryptedInfo = encryptedInfoThreadLocal.get();
        GenericResponse genericResponse = (GenericResponse) pjp.proceed();

        if (ResponseStatus.SC_NOT_MODIFIED.getValue() == genericResponse.getHttpCode()) {
            reports(
                    ProcessStatusEnum.CACHE,
                    "缓存数据",
                    SDF.format(new Date(System.currentTimeMillis())),
                    encryptedInfo.getId()
            );
        } else if (ResponseStatus.SC_OK.getValue() == genericResponse.getHttpCode()
                && ResponseStatus.SC_PROCESS_SUCCESS.getValue() == genericResponse.getData().getProcessStatus()) {
            reports(
                    ProcessStatusEnum.OK,
                    "处理成功",
                    SDF.format(new Date(System.currentTimeMillis())),
                    encryptedInfo.getId()
            );
        } else if (ResponseStatus.SC_OK.getValue() == genericResponse.getHttpCode()
                && ResponseStatus.SC_PROCESS_SUCCESS.getValue() != genericResponse.getData().getProcessStatus()) {
            reports(
                    ProcessStatusEnum.NO,
                    genericResponse.getData().getProcessMsg(),
                    SDF.format(new Date(System.currentTimeMillis())),
                    encryptedInfo.getId()
            );
        } else {
            reports(
                    ProcessStatusEnum.UNKNOWN,
                    "其他",
                    SDF.format(new Date(System.currentTimeMillis())),
                    encryptedInfo.getId());
        }

        return genericResponse;
    }

    private void reports(Enum<ProcessStatusEnum> processStatusEnumEnum, String message, String creationTime, int aid) {

        processStatusService.save(new ProcessStatus(
                        null,
                        processStatusEnumEnum,
                        message != null ? message : "原异常信息为 null，无法确定异常信息",
                        creationTime,
                        aid
                )
        );
    }
}
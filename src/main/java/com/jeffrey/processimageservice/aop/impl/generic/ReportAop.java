package com.jeffrey.processimageservice.aop.impl.generic;


import com.jeffrey.processimageservice.entities.DemoRequestParams;
import com.jeffrey.processimageservice.entities.ProcessStatus;
import com.jeffrey.processimageservice.entities.enums.ProcessStatusEnum;
import com.jeffrey.processimageservice.entities.enums.ResponseStatus;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.service.ProcessStatusService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Aspect
@Order(101)
@Component
@Slf4j
public class ReportAop {

    private final ProcessStatusService processStatusService;
    private final ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal;
    private static final ThreadLocal<Integer> AID_OR_PAU_ID = new ThreadLocal<>();

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Autowired
    public ReportAop(ProcessStatusService processStatusService, ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal) {
        this.processStatusService = processStatusService;
        this.encryptedInfoThreadLocal = encryptedInfoThreadLocal;
    }

    @Pointcut(value = "@annotation(com.jeffrey.processimageservice.aop.annotation.generic.Report)")
    public void pointCut() {
    }

    @AfterThrowing(value = "pointCut()", throwing = "ex")
    public void reportException(Exception ex) {
        EncryptedInfo encryptedInfo = encryptedInfoThreadLocal.get();
        int id = encryptedInfo == null ? -1 : encryptedInfo.getId();

        reports(
                ProcessStatusEnum.NO,
                ex.getMessage(),
                "",
                SDF.format(new Date(System.currentTimeMillis())),
                id
        );
    }

    @Before(value = "pointCut()")
    public void threadLocalSet(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof DemoRequestParams) {
            String openid = ((DemoRequestParams) args[0]).getOpenid();
            Integer publicAccountUserId = processStatusService.selectIdByUserOpenId(openid);
            AID_OR_PAU_ID.set(publicAccountUserId);
        } else {
            EncryptedInfo encryptedInfo = encryptedInfoThreadLocal.get();
            Integer aid = encryptedInfo.getId();
            AID_OR_PAU_ID.set(aid);
        }
    }

    @AfterReturning(value = "pointCut()", returning = "genericResponse")
    public Object report(GenericResponse genericResponse) {

        String message;
        ProcessStatusEnum status;
        Date date = new Date(System.currentTimeMillis());

        try {

            int aid = AID_OR_PAU_ID.get();

            int code = genericResponse.getHttpCode();


            if (ResponseStatus.SC_NOT_MODIFIED.getValue() == code) {
                status = ProcessStatusEnum.CACHE;
                message = "缓存数据";
            } else if (ResponseStatus.SC_OK.getValue() == code) {
                int processStatus = genericResponse.getData().getProcessStatus();
                if (ResponseStatus.SC_PROCESS_SUCCESS.getValue() == processStatus) {
                    status = ProcessStatusEnum.OK;
                    message = "处理成功";
                } else {
                    status = ProcessStatusEnum.NO;
                    message = genericResponse.getData().getProcessMsg();
                }
            } else {
                status = ProcessStatusEnum.UNKNOWN;
                message = "其他";
            }
            String sign;
            EncryptedInfo encryptedInfo = genericResponse.getEncryptedInfo();
            if (encryptedInfo == null) {
                sign = "PUBLIC";
            } else {
                sign = genericResponse.getEncryptedInfo().getSign();
            }

            reports(status, message, sign, SDF.format(date), aid);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            AID_OR_PAU_ID.remove();
        }

        return genericResponse;
    }

    private void reports(Enum<ProcessStatusEnum> processStatusEnumEnum, String message, String sign, String creationTime, int aid) {

        processStatusService.save(new ProcessStatus(
                        null,
                        processStatusEnumEnum,
                        message != null ? message : "原异常信息为 null，无法确定异常信息",
                        sign,
                        creationTime,
                        aid
                )
        );
    }
}
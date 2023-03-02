package com.jeffrey.processimageservice.aop.impl.api;

import com.jeffrey.processimageservice.entities.enums.AccountStatus;
import com.jeffrey.processimageservice.entities.enums.ResponseStatus;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.service.AccountService;
import com.jeffrey.processimageservice.strategy.StrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.entities.sign.AccountInfo;
import com.jeffrey.processimageservice.exception.exception.clitent.AccountException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对账户的使用次数进行扣除
 *
 * @author jeffrey
 */
@Component
@Slf4j
@Aspect
public class UpdateAccountUsedAop {

    private final AccountService accountService;
    private final ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal;
    private static final SimpleDateFormat SDF;
    private static final ReentrantLock REENTRANT_LOCK;
    private static final ArrayList<Integer> RELEASE_HTTP_STATUS_CODE;
    private static final ArrayList<Integer> RELEASE_PROCESS_STATUS_CODE;

    static {

        RELEASE_HTTP_STATUS_CODE = new ArrayList<>();
        RELEASE_PROCESS_STATUS_CODE = new ArrayList<>();

        REENTRANT_LOCK = new ReentrantLock();
        SDF = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");

        // 不扣除使用次数的状态码集合
        RELEASE_HTTP_STATUS_CODE.add(ResponseStatus.SC_NOT_MODIFIED.getValue());
        RELEASE_HTTP_STATUS_CODE.add(ResponseStatus.SC_OK.getValue());

        RELEASE_PROCESS_STATUS_CODE.add(ResponseStatus.SC_PROCESS_SUCCESS.getValue());
        RELEASE_PROCESS_STATUS_CODE.add(ResponseStatus.SC_PROCESS_WHITE_IMAGE.getValue());
    }

    @Autowired
    public UpdateAccountUsedAop(AccountService accountService, ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal) {
        this.accountService = accountService;
        this.encryptedInfoThreadLocal = encryptedInfoThreadLocal;
    }

    @Pointcut("@annotation(com.jeffrey.processimageservice.aop.annotation.api.UpdateAccountUsed)")
    private void updateAccountUsedAopCut() {
    }

    @Around(value = "updateAccountUsedAopCut()")
    @Order(value = 2)
    public Object updateAccountUsedCount(ProceedingJoinPoint pjp) throws Throwable {
        REENTRANT_LOCK.lock();
        try {

            EncryptedInfo encryptedInfo = encryptedInfoThreadLocal.get()
                    != null ? encryptedInfoThreadLocal.get() : (EncryptedInfo) pjp.getArgs()[2];

            AccountInfo accountInfo = accountService.getAccountInfoById(encryptedInfo.getId());

            if (accountInfo.getApiCanUseCount() <= AccountStatus.ZERO_USED.getValue()) {
                throw new AccountException("账户可用次数不足");
            }

            // 在调用连接点方法之前不修改账户信息
            GenericResponse genericResponse = (GenericResponse) pjp.proceed();

            boolean shouldNotModifyCounts = StrategyFactory.shouldNotModifyCounts(genericResponse);

            accountInfo.setApiCanUseCount(shouldNotModifyCounts ? accountInfo.getApiCanUseCount() : accountInfo.getApiCanUseCount() - 1);
            accountInfo.setApiUsedCount(shouldNotModifyCounts ? accountInfo.getApiUsedCount() : accountInfo.getApiUsedCount() + 1);

            accountInfo.setLastModifyTime(SDF.format(new Date(System.currentTimeMillis())));
            accountService.updateAccountInfo(accountInfo);

            genericResponse.setRemainingUsage(accountInfo.getApiCanUseCount());
            genericResponse.setAllUsedCount(accountInfo.getApiUsedCount());
            encryptedInfo.setPrivateSecret(null);
            encryptedInfo.setPublicKey(null);
            genericResponse.setEncryptedInfo(encryptedInfo);

            return genericResponse;

        } finally {

            REENTRANT_LOCK.unlock();

        }
    }
}
package com.jeffrey.processimageservice.aop.impl.api;

import com.jeffrey.processimageservice.enums.AccountStatus;

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

import java.time.LocalDateTime;

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
    private static final ReentrantLock REENTRANT_LOCK;

    static {
        REENTRANT_LOCK = new ReentrantLock();
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

            // 判断账户是否有长期和限期次数
            LocalDateTime expireTimes = accountInfo.getLimitedTermExpireTimes();
            if (accountInfo.getLongTermUsageCount() <= AccountStatus.ZERO_USED.getValue()
                    && (accountInfo.getLimitedTermUsageCount() <= AccountStatus.ZERO_USED.getValue()
                    || LocalDateTime.now().isAfter(expireTimes))
            ) {
                throw new AccountException("账户可用次数不足");
            }

            // 在调用连接点方法之前不修改账户信息
            GenericResponse genericResponse = (GenericResponse) pjp.proceed();

            boolean modify = !StrategyFactory.shouldNotModifyCounts(genericResponse);
            boolean priorityDeduction = accountInfo.getLimitedTermUsageCount() > 0
                    && LocalDateTime.now().isBefore(expireTimes);

            if (modify) {
                // 需扣除次数

                if (priorityDeduction) {
                    // 优先扣除限期套餐次数
                    accountInfo.setLimitedTermUsageCount(accountInfo.getLimitedTermUsageCount() - 1);
                } else {
                    // 扣除长期套餐可用次数
                    accountInfo.setLongTermUsageCount(accountInfo.getLongTermUsageCount() - 1);
                }

                accountInfo.setCallSuccessful(accountInfo.getCallSuccessful() + 1);
                accountInfo.setLastModify(LocalDateTime.now());

                accountService.updateAccountInfo(accountInfo);
            }


            // 剩余次数为限期套餐次数 + 长期套餐次数
            genericResponse.setRemainingUsage(accountInfo.getLongTermUsageCount() + accountInfo.getLimitedTermUsageCount());
            genericResponse.setCallSuccessful(accountInfo.getCallSuccessful());
            genericResponse.setLongTermUsageCount(accountInfo.getLongTermUsageCount());
            genericResponse.setLimitedTermUsageCount(accountInfo.getLimitedTermUsageCount());
            genericResponse.setLimitedTermExpireTimes(accountInfo.getLimitedTermExpireTimes());

            encryptedInfo.setAppSecret(null);
            encryptedInfo.setAppId(null);
            encryptedInfo.setId(null);

            genericResponse.setEncryptedInfo(encryptedInfo);

            return genericResponse;

        } finally {

            REENTRANT_LOCK.unlock();

        }
    }
}
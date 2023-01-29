package com.jeffrey.processimageservice.aop.impl;

import com.google.gson.Gson;
import com.jeffrey.processimageservice.entities.RequestParams;
import com.jeffrey.processimageservice.entities.RequestParamsWrapper;
import com.jeffrey.processimageservice.entities.enums.ResponseStatus;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Aspect
@Slf4j
public class CacheProcessedResponseAop {
    private final ThreadLocal<RequestParamsWrapper> requestParamsWrapperThreadLocal;
    private final ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CacheProcessedResponseAop(ThreadLocal<RequestParamsWrapper> requestParamsWrapperThreadLocal, ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal, RedisTemplate<String, Object> redisTemplate) {
        this.requestParamsWrapperThreadLocal = requestParamsWrapperThreadLocal;
        this.encryptedInfoThreadLocal = encryptedInfoThreadLocal;
        this.redisTemplate = redisTemplate;
    }

    @Pointcut("@annotation(com.jeffrey.processimageservice.aop.annotation.CacheProcessedResponse)")
    private void cacheProcessedResponseAop() {
    }

    @Around(value = "cacheProcessedResponseAop()")
    @Order(value = 1)
    public Object cache(ProceedingJoinPoint pjp) throws Throwable {

        // 是否为回调任务
        Object isAsyncTask = pjp.getArgs()[1];

        EncryptedInfo encryptedInfo;
        RequestParamsWrapper requestParamsWrapper;

        // 因回调线程导致 requestParamsWrapper 和 encryptedInfo 失效，确保这两个参数可用
        if (isAsyncTask instanceof Boolean && isAsyncTask.equals(true)) {
            requestParamsWrapper = (RequestParamsWrapper) pjp.getArgs()[0];
            encryptedInfo = (EncryptedInfo) pjp.getArgs()[2];
        } else {
            requestParamsWrapper = requestParamsWrapperThreadLocal.get();
            encryptedInfo = encryptedInfoThreadLocal.get();
        }

        RequestParams requestParams = requestParamsWrapper.getRequestParams();

        if (requestParams.getSync() != null && requestParams.getSync().equals(true) && isAsyncTask.equals(false)) {
            return pjp.proceed();
        }

        String signatureParamsToString = requestParamsWrapper.getRequestParams().signatureParamsToString();
        String imageUniqueIdentification = encryptedInfo.getImageUniqueIdentification();
        String imageId = DigestUtils.md5Hex(signatureParamsToString + imageUniqueIdentification);

        if (redisTemplate.hasKey(imageId)) {

            ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();

            GenericResponse genericResponse = new Gson().fromJson(String.valueOf(opsForValue.get(imageId)), GenericResponse.class);

            genericResponse.setHttpCode(ResponseStatus.SC_NOT_MODIFIED.getValue());
            genericResponse.setHttpMsg("本次响应为缓存数据，未作任何修改");
            genericResponse.setRemainingUsage(-1);
            genericResponse.setAllUsedCount(-1);

            return pjp.proceed(new Object[]{requestParamsWrapper, isAsyncTask, encryptedInfo, genericResponse});

        } else {

            GenericResponse genericResponse = (GenericResponse) pjp.proceed();

            // 深拷贝一个对象，避免二次加工造成缓存标识不一致
            requestParamsWrapper.setRequestParamsClone(requestParamsWrapper.getRequestParams().clone());

            RequestParams originRequestParams = requestParamsWrapper.getRequestParamsClone();
            if (originRequestParams != null) {
                if (genericResponse.getData().getProcessStatus() != ResponseStatus.SC_PROCESS_ASYNC_REQUEST.getValue()) {
                    if (!redisTemplate.hasKey(imageId)) {
                        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
                        opsForValue.set(imageId, new Gson().toJson(genericResponse));
                        redisTemplate.expire(imageId, 5, TimeUnit.HOURS);
                    }
                }
            }

            return genericResponse;
        }

    }
}

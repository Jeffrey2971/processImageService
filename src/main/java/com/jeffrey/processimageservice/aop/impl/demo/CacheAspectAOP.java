package com.jeffrey.processimageservice.aop.impl.demo;

import com.google.gson.Gson;
import com.jeffrey.processimageservice.entities.DemoRequestParams;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Aspect
@Component
public class CacheAspectAOP {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public CacheAspectAOP(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Pointcut("@annotation(com.jeffrey.processimageservice.aop.annotation.demo.CacheAspect)")
    private void pointCut() {
    }

    @Around(value = "pointCut()")
    public Object processCacheData(ProceedingJoinPoint pjp) throws Throwable {

        DemoRequestParams requestParams = ((DemoRequestParams) pjp.getArgs()[0]);

        Object[] cachedResult = getCache(requestParams.getFile(), requestParams.getRect(), requestParams.getOpenid());
        String cachedKey = (String) cachedResult[0];
        GenericResponse cachedGenericResponse = (GenericResponse) cachedResult[1];

        if (cachedGenericResponse != null) {
            return cachedGenericResponse;
        } else {
            Object proceedRes = pjp.proceed();
            doCache(cachedKey, ((GenericResponse) proceedRes), 5, TimeUnit.HOURS);
            return proceedRes;
        }
    }

    private Object[] getCache(MultipartFile requestFile, LinkedList<String> rect, String openid) throws IOException {

        byte[] requestFileBytes = FileCopyUtils.copyToByteArray(requestFile.getInputStream());
        String requestFileMd5 = DigestUtils.md5DigestAsHex(requestFileBytes);
        String requestFileSelectRect = rect.toString();
        String key = genericCacheKey(requestFileMd5, requestFileSelectRect, openid);
        Object obj = redisTemplate.opsForValue().get(key);

        GenericResponse genericResponse = null;

        if (obj != null) {
            String objStr = (String) obj;
            genericResponse = new Gson().fromJson(objStr, GenericResponse.class);
        }

        return new Object[]{key, genericResponse};
    }

    private String genericCacheKey(String requestFileMd5, String requestFileSelectRect, String openid) {
        return DigestUtils.md5DigestAsHex((requestFileMd5 + requestFileSelectRect + openid).getBytes(StandardCharsets.UTF_8));
    }

    private void doCache(String key, GenericResponse genericResponse, int expire, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, new Gson().toJson(genericResponse));
        redisTemplate.expire(key, expire, timeUnit);
    }
}

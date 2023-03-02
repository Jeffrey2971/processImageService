package com.jeffrey.processimageservice.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class AsyncConfiguration implements AsyncConfigurer {

    private final ThreadPoolTaskExecutor myTaskExecutor;

    @Autowired
    public AsyncConfiguration(ThreadPoolTaskExecutor myTaskExecutor) {
        this.myTaskExecutor = myTaskExecutor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return myTaskExecutor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("--------------线程抛出异常--------------");
            log.error("信息：" + ex.getMessage());
            log.error("目标方法：" +  method.getClass().getTypeName());
            log.error("目标方法参数：" + Arrays.toString(params));
        };
    }
}


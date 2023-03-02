package com.jeffrey.processimageservice.service.impl;

import com.google.gson.Gson;
import com.jeffrey.processimageservice.entities.AsyncPendingTasksItem;
import com.jeffrey.processimageservice.entities.Info;
import com.jeffrey.processimageservice.entities.enums.ResponseStatus;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.service.ProcessService;
import com.jeffrey.processimageservice.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class AsyncProcess implements ApplicationRunner {
    private final ProcessService processService;
    private final Info info;
    private final LinkedBlockingQueue<AsyncPendingTasksItem> asyncPendingTasksItemLinkedBlockingQueue;
    private final ThreadPoolTaskExecutor myTaskExecutor;

    @Autowired
    public AsyncProcess(ProcessService processService, Info info, LinkedBlockingQueue<AsyncPendingTasksItem> asyncPendingTasksItemLinkedBlockingQueue, ThreadPoolTaskExecutor myTaskExecutor) {
        this.processService = processService;
        this.info = info;
        this.asyncPendingTasksItemLinkedBlockingQueue = asyncPendingTasksItemLinkedBlockingQueue;

        this.myTaskExecutor = myTaskExecutor;
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run(ApplicationArguments args) {

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setContentLanguage(Locale.CHINESE);
        httpHeaders.setHost(InetSocketAddress.createUnresolved(info.getServerDomain(), 80));
        httpHeaders.add("connection", "keep-alive");
        httpHeaders.add("user-agent", null);
        httpHeaders.add("accept", null);

        while (true) {

            try {

                log.info("sync queue size：{}", asyncPendingTasksItemLinkedBlockingQueue.size());
                AsyncPendingTasksItem asyncPendingTasksItem = asyncPendingTasksItemLinkedBlockingQueue.take();

                myTaskExecutor.execute(new Runnable() {

                    @Override
                    public void run() {
                        // 这里是一个新的线程，在调用 taskAllocation 任务时应注意 ThreadLocal<> 在新的线程取值问 null 的问题
                        // 注意 taskAllocation 任务执行完后被 @AfterReturning 切入时拿 ThreadLocal<EncryptedInfo> 为 null 的问题
                        GenericResponse genericResponse = processService.taskAllocation(
                                asyncPendingTasksItem.getRequestParamsWrapper(),
                                true,
                                asyncPendingTasksItem.getEncryptedInfo(),
                                null
                        );

                        String callback = asyncPendingTasksItem.getRequestParamsWrapper().getRequestParams().getCallback();

                        httpHeaders.setContentLength(genericResponse.toString().getBytes().length);

                        HttpEntity<String> stringHttpEntity = new HttpEntity<>(new Gson().toJson(genericResponse), httpHeaders);

                        ResponseEntity<String> response = RequestUtil.postEntity(callback, stringHttpEntity, String.class, null);

                        if (response.getStatusCodeValue() != ResponseStatus.SC_OK.getValue()) {
                            log.warn("callback failed, try again.");
                            log.warn(response.getBody() + response.getStatusCodeValue());
                            response = RequestUtil.postEntity(callback, stringHttpEntity, String.class, null);

                            if (response.getStatusCodeValue() != ResponseStatus.SC_OK.getValue()) {
                                log.warn(response.getBody() + response.getStatusCodeValue());
                                log.warn("callback failed.");
                            }
                        }
                    }
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

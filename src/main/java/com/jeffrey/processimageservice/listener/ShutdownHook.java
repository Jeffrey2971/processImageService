package com.jeffrey.processimageservice.listener;

import com.jeffrey.processimageservice.entities.AsyncPendingTasksItem;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class ShutdownHook implements ApplicationListener<ContextClosedEvent> {

    private static final String URL = "https://www.processimage.cn";

    private volatile boolean isShuttingDown = false;
    private final LinkedBlockingQueue<AsyncPendingTasksItem> asyncPendingTasksItemLinkedBlockingQueue;
    private final ThreadPoolTaskExecutor myTaskExecutor;


    @Autowired
    public ShutdownHook(LinkedBlockingQueue<AsyncPendingTasksItem> asyncPendingTasksItemLinkedBlockingQueue, LinkedBlockingQueue<AsyncPendingTasksItem> linkedBlockingQueue, ThreadPoolTaskExecutor myTaskExecutor) {
        this.asyncPendingTasksItemLinkedBlockingQueue = asyncPendingTasksItemLinkedBlockingQueue;

        this.myTaskExecutor = myTaskExecutor;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.warn("监听到关闭信号，执行 JVM 关闭钩子,将拒绝所有新的请求");

        isShuttingDown = true;

        while (!asyncPendingTasksItemLinkedBlockingQueue.isEmpty()) {
            log.warn("剩余 {} 个任务未处理，等待关闭", asyncPendingTasksItemLinkedBlockingQueue.size());
            Thread.sleep(2000);
        }

        myTaskExecutor.shutdown();

        log.info("平滑期，60 秒后关闭服务");
        Thread.sleep(60 * 1000);
    }

    public boolean serverIsShuttingDown(){
        return this.isShuttingDown;
    }

}

package com.jeffrey.processimageservice.service.impl;

import lombok.Getter;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * 配合 try-with-resources 语法糖安全关闭 Process 流
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Getter
public class ProcessBuilderWrapper implements AutoCloseable{

    private final Process process;

    public ProcessBuilderWrapper(Process process) {
        this.process = process;
    }

    public InputStream getProcessInputStream(){
        return this.process.getInputStream();
    }

    public ProcessBuilderWrapper setWaitFor(long timeout, TimeUnit timeUnit) throws InterruptedException {
        this.process.waitFor(timeout, timeUnit);
        return this;
    }

    @Override
    public void close() throws Exception {
        // ---------- ignore ---------- //
    }
}

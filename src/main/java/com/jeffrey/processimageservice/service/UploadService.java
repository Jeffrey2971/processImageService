package com.jeffrey.processimageservice.service;

import com.jeffrey.processimageservice.entities.RequestParams;
import com.jeffrey.processimageservice.entities.RequestParamsWrapper;
import com.jeffrey.processimageservice.exception.exception.ArgumentsOverwriteException;
import org.springframework.stereotype.Service;

@Service
public interface UploadService {

    /**
     * 重写 SpringBoot 封装的请求参数，并判断提供的参数是否正确，遵循 fast failed
     * 规则：如果没指定参数则使用默认值，如果指定了参数则使用指定的参数，如设定了指定参数但有误则抛出 ArgumentsOverwriteException 异常
     * @param params RequestParamsWrapper 对 RequestParams 进行了包装
     */
    RequestParamsWrapper argumentsOverwrite(RequestParams params) throws ArgumentsOverwriteException;


}

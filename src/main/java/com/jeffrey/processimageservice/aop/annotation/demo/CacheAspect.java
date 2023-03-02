package com.jeffrey.processimageservice.aop.annotation.demo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 检查请求是否有缓存并对响应进行缓存
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheAspect {}
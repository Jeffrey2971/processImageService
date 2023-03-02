package com.jeffrey.processimageservice.aop.annotation.demo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对每位公众号用户进行调用次数统计、更新，拦截使用次数位 0 的用户
 *
 * @author jeffrey
 * @since JDK 1.8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UpdatePublicAccountUserData {}

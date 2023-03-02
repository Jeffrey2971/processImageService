package com.jeffrey.processimageservice.aop.annotation.demo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对公众号用户首次使用时初始化数据
 *
 * @author jeffrey
 * @since JDK 1.8
 */


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface InitPublicAccountUserAspect {}

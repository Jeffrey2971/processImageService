package com.jeffrey.processimageservice.entities.enums;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public enum ProcessStatusEnum {
    /**
     * 处理成功，非缓存
     */
    OK,
    /**
     * 处理失败
     */
    NO,
    /**
     * 处理成功，缓存
     */
    CACHE,
    /**
     * 未知结果
     */
    UNKNOWN
}

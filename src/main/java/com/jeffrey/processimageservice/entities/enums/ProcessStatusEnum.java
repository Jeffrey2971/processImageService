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
     * 相同的密码，无需修改
     */
    SAME,

    /**
     * 提供的当前密码非法
     */
    CURRENT_PASSWORD_INVALID,
    /**
     * 邮箱地址不存在
     */
    MAIL_NOT_EXISTS,
    /**
     * 用户不存在
     */
    USER_NOT_EXISTS,
    /**
     * 验证不通过
     */
    VERIFICATION_FAILED,

    /**
     * 参数为空
     */
    PARAM_EMPTY,
    /**
     * 不安全的参数
     */
    PARAM_NOT_SAFE,
    /**
     * token 失效或不存在
     */
    TOKEN_INVALID_OR_NOT_EXIST,

    /**
     * token 不是首次访问
     */
    TOKEN_NOT_FIRST_ACCESS,
    /**
     * 服务器异常
     */
    SERVER_ERROR,
    /**
     * 未知结果
     */
    UNKNOWN
}

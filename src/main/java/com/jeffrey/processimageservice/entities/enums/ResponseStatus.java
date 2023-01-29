package com.jeffrey.processimageservice.entities.enums;

/**
 * @author jeffrey
 */

public enum ResponseStatus {

    // ------------------------ HTTP STATUS ------------------------ //

    /**
     * {@code 200 OK} (HTTP/1.0 - RFC 1945)
     * <p>
     * 请求成功，但处理不一定成功，具体看 data 对象中的 processStatus
     */
    SC_OK(200),

    /**
     * {@code 304 Not Modified} (HTTP/1.0 - RFC 1945)
     * <p>
     * 自上次的请求以来本次响应未做任何修改
     */
    SC_NOT_MODIFIED(304),

    /**
     * {@code 400 Bad Request} (HTTP/1.1 - RFC 2616)
     * <p>
     * 请求参数错误
     */
    SC_BAD_REQUEST(400),

    /**
     * {@code 401 Unauthorized} (HTTP/1.0 - RFC 1945)
     * <p>
     * 认证失败
     */
    SC_UNAUTHORIZED(401),

    /**
     * {@code 403 Forbidden} (HTTP/1.0 - RFC 1945)
     * <p>
     * 拒绝请求
     */
    SC_FORBIDDEN(403),

    /**
     * {@code 405 Method Not Allowed} (HTTP/1.1 - RFC 2616)
     * <p>
     * 请求方法不被允许
     */
    SC_METHOD_NOT_ALLOWED(405),

    /**
     * {@code 413 Request Entity Too Large} (HTTP/1.1 - RFC 2616)
     * <p>
     * 上传文件大小超出限制
     */
    SC_REQUEST_TOO_LONG(413),

    /**
     * {@code 429 Request Entity Too Large} (HTTP/1.1 - RFC 2616)
     * <p>
     * 请求频率超出 QPS 限制（默认 QPS 为 2）
     */
    SC_TOO_MANY_REQUEST(429),

    /**
     * {@code 500 Server Error} (HTTP/1.0 - RFC 1945)
     * <p>
     * 服务端系统出错
     */
    SC_INTERNAL_SERVER_ERROR(500),

    // ---------------------- RESPONSE STATUS ---------------------- //

    /**
     * {@code 0 OK}
     * <p>
     * 处理成功
     */
    SC_PROCESS_SUCCESS(0),
    /**
     * {@code 52000 ASYNC REQUEST}
     * <p>
     * 异步调用
     */
    SC_PROCESS_ASYNC_REQUEST(52000),
    /**
     * {@code 52001 UNHANDLED}
     * <p>
     * 因客户端问题导致的未处理
     */
    SC_PROCESS_UNHANDLED(52001),
    /**
     * {@code 52002 WHITE IMAGE}
     * <p>
     * 请求没有明确指定矩形坐标且无法获取图片中的水印内容
     */
    SC_PROCESS_WHITE_IMAGE(52002),
    /**
     * {@code 52003 REQUEST TIMEOUT}
     * <p>
     * 请求超时
     */
    SC_PROCESS_REQUEST_TIME_OUT(52003),
    /**
     * {@code 52004 PROCESS FAILED}
     * <p>
     * 服务端系统出错
     */
    SC_PROCESS_FAILED(52004),
    /**
     * {@code 52005 INVALID OFFSET}
     * <p>
     * 提供了非法的偏移量
     */
    SC_PROCESS_INVALID_OFFSET(52005),

    /**
     * {@code 52006 ONLY OCR}
     * <p>
     * 仅识别图像信息，不对水印做出处理
     */
    SC_PROCESS_ONLY_OCR(52006);

    // ----------------------- ASYNC STATUS ----------------------- //


    private final int value;

    private ResponseStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

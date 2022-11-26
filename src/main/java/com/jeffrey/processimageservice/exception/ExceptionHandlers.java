package com.jeffrey.processimageservice.exception;

import com.jeffrey.processimageservice.entitys.Data;
import com.jeffrey.processimageservice.entitys.Response;
import com.jeffrey.processimageservice.exception.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import com.jeffrey.processimageservice.utils.GetRequestAddressUtil;

import javax.servlet.http.HttpServletRequest;


/**
 * @author jeffrey
 * @since JDK 1.8
 */

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response methodException(Exception e, HttpServletRequest request) {
        
        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new Response(405, "FAILED::非法请求", "json", -1, "ExceptionHandler[HttpRequestMethodNotSupportedException.class]", ipAddress, new Data(52001, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public Response paramsException(Exception e, HttpServletRequest request) {
        
        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new Response(400, "FAILED::缺少参数", "json", -1, "ExceptionHandler[MissingServletRequestPartException.class]", ipAddress, new Data(52001, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(ArgumentsOverwriteException.class)
    public Response argumentsOverwriteException(Exception e, HttpServletRequest request) {
        
        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new Response(400, "FAILED::" + e.getLocalizedMessage(), "json", -1, "ExceptionHandler[" + e.getClass().getSimpleName() + ".class]", ipAddress, new Data(52001, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(UnknownArgumentException.class)
    public Response unknownArgumentException(Exception e, HttpServletRequest request) {
        
        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new Response(400, "FAILED::" + e.getLocalizedMessage(), "json", -1, "ExceptionHandler[UnknownArgumentException.class]", ipAddress, new Data(52001, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(QPSException.class)
    public Response qpsException(Exception e, HttpServletRequest request) {
        
        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new Response(429, "FAILED::" + e.getLocalizedMessage(), "json", -1, "ExceptionHandler[QPSException.class]", ipAddress, new Data(52001, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(FileTooLargeException.class)
    public Response fileTooLargeException(Exception e, HttpServletRequest request) {

        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new Response(413, "FAILED::" + e.getLocalizedMessage(), "json", -1, "ExceptionHandler[FileTooLargeException.class]", ipAddress, new Data(52001, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(ImageTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Response imageTypeNotSupportedException(Exception e, HttpServletRequest request) {
        
        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new Response(400, "FAILED::" + e.getLocalizedMessage(), "json", -1, "ExceptionHandler[ImageTypeNotSupportedException.class]", ipAddress, new Data(52001, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response baseException(Exception e, HttpServletRequest request) {
        
        e.printStackTrace();
        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new Response(500, "FAILED::服务端异常", "json", -1, "ExceptionHandler[Exception.class]", ipAddress, new Data(52001, "UNHANDLED::因其他问题导致未处理", null, null));
    }
}

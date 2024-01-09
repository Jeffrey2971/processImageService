package com.jeffrey.processimageservice.exception;

import com.jeffrey.processimageservice.entities.response.Data;
import com.jeffrey.processimageservice.entities.FailedItem;
import com.jeffrey.processimageservice.entities.register.RegisterResultResponse;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.entities.response.ResponseObject;
import com.jeffrey.processimageservice.exception.exception.*;
import com.jeffrey.processimageservice.exception.exception.clitent.*;
import com.jeffrey.processimageservice.exception.exception.server.ServiceShuttingDownException;
import com.jeffrey.processimageservice.exception.exception.server.UnknownArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import com.jeffrey.processimageservice.utils.GetRequestAddressUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author jeffrey
 * @since JDK 1.8
 */
@Component
@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(ServiceShuttingDownException.class)
    public GenericResponse serverResponse(HttpServletRequest request){
        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(503, "SHUTTING::正在停止，服务收到了系统发出的关闭指令，正在执行 JVM 关闭钩子，目前有剩余的任务未完成，服务器拒绝了您的请求", "json", null, null,  "ExceptionHandler[HttpRequestMethodNotSupportedException.class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public GenericResponse methodException(Exception e, HttpServletRequest request) {

        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(405, "FAILED::非法请求", "json", null, null,  "ExceptionHandler[HttpRequestMethodNotSupportedException.class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }
    @ExceptionHandler(RegisterException.class)
    public RegisterResultResponse registerException(Exception e, HttpServletRequest request) {
        RegisterException registerException = (RegisterException) e;
        List<FailedItem> failedItems = registerException.getFailedItems();

        return new RegisterResultResponse(400, "FAILED::" + e.getLocalizedMessage(), failedItems);
    }

    @ExceptionHandler(SignatureFailedException.class)
    public GenericResponse signatureFailedException(Exception e, HttpServletRequest request) {

        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(401, "FAILED::" + e.getLocalizedMessage(), "json", null, null,  "ExceptionHandler[SignatureFailedException.class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(AccountPasswordResetException.class)
    public ResponseObject accountPasswordResetException(Exception e, HttpServletRequest request) {
        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return ResponseObject.fail("FAILED::非法 token", 403);
    }

    @ExceptionHandler(PublicAccountExperienceAccessException.class)
    public GenericResponse publicAccountExperienceAccessException(Exception e, HttpServletRequest request){
        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(401, "FAILED::" + e.getLocalizedMessage(), "json", null, null, "ExceptionHandler[PublicAccountExperienceAccessException.class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public GenericResponse paramsException(Exception e, HttpServletRequest request) {

        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(400, "FAILED::缺少参数", "json", null, null,  "ExceptionHandler[MissingServletRequestPartException.class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(ArgumentsOverwriteException.class)
    public GenericResponse argumentsOverwriteException(Exception e, HttpServletRequest request) {

        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(400, "FAILED::" + e.getLocalizedMessage(), "json", null, null,  "ExceptionHandler[" + e.getClass().getSimpleName() + ".class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(UnknownArgumentException.class)
    public GenericResponse unknownArgumentException(Exception e, HttpServletRequest request) {

        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(400, "FAILED::" + e.getLocalizedMessage(), "json", null, null,  "ExceptionHandler[UnknownArgumentException.class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(QPSException.class)
    public GenericResponse qpsException(Exception e, HttpServletRequest request) {

        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(429, "FAILED::" + e.getLocalizedMessage(), "json", null, null,  "ExceptionHandler[QPSException.class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler({FileTooLargeException.class, MaxUploadSizeExceededException.class})
    public GenericResponse fileTooLargeException(Exception e, HttpServletRequest request) {

        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(413, "FAILED::" + e.getLocalizedMessage(), "json", null, null,  "ExceptionHandler[FileTooLargeException.class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }


    @ExceptionHandler(AccountException.class)
    public GenericResponse accountException(Exception e, HttpServletRequest request) {

        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(403, "FAILED::" + e.getLocalizedMessage(), "json", null, null,  "ExceptionHandler[AccountException.class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(ImageTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public GenericResponse imageTypeNotSupportedException(Exception e, HttpServletRequest request) {

        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(400, "FAILED::" + e.getLocalizedMessage(), "json", null, null,  "ExceptionHandler[ImageTypeNotSupportedException.class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(ProductNotFountException.class)
    public GenericResponse productNotFountException(ProductNotFountException e, HttpServletRequest request) {
        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(400, "FAILED::" + e.getLocalizedMessage(), "json", null, null,  "ExceptionHandler[" + e.getClass().getSimpleName() + ".class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(InvalidPaymentTypeException.class)
    public GenericResponse invalidPaymentTypeException(InvalidPaymentTypeException e, HttpServletRequest request) {
        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(400, "FAILED::" + e.getLocalizedMessage(), "json", null, null,  "ExceptionHandler[" + e.getClass().getSimpleName() + ".class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GenericResponse baseException(Exception e, HttpServletRequest request) {

        e.printStackTrace();
        String ipAddress = GetRequestAddressUtil.getIPAddress(request);
        return new GenericResponse(500, "FAILED::服务端异常", "json", null, null,  "ExceptionHandler[Exception.class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
    }
}

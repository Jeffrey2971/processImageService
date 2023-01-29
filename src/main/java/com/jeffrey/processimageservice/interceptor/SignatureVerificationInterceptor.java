package com.jeffrey.processimageservice.interceptor;

import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.entities.sign.SignatureParams;
import com.jeffrey.processimageservice.exception.exception.clitent.SignatureFailedException;
import com.jeffrey.processimageservice.service.SignatureVerificationInterceptorService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class SignatureVerificationInterceptor implements HandlerInterceptor {

    private final ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal;

    private final SignatureVerificationInterceptorService signatureVerificationInterceptorService;

    @Autowired
    public SignatureVerificationInterceptor(ThreadLocal<EncryptedInfo> encryptedInfoThreadLocal, SignatureVerificationInterceptorService signatureVerificationInterceptorService) {
        this.encryptedInfoThreadLocal = encryptedInfoThreadLocal;
        this.signatureVerificationInterceptorService = signatureVerificationInterceptorService;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {

        if ("GET".equals(request.getMethod())) {
            return true;
        }

        String sign;
        if ("demo".equals(request.getParameter("referrer"))) {
            sign = request.getParameter("demo_sign");
        } else {
            sign = request.getHeader("x-jeffrey-api-sign");
        }

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile uploadFile = multipartRequest.getFile("uploadFile");

        String publicKey = request.getParameter("demo_public_key");
        String salt = request.getParameter("demo_salt");

        if (!StringUtils.isBlank(sign) && uploadFile != null && !StringUtils.isBlank(salt) && StringUtils.isNumeric(salt)) {
            InputStream inputStream = uploadFile.getInputStream();
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            SignatureParams signatureParams = new SignatureParams(publicKey, Integer.parseInt(salt), bytes);

            EncryptedInfo encryptedInfo = signatureVerificationInterceptorService.signature(signatureParams);

            if (encryptedInfo != null && sign.equals(encryptedInfo.getSign())) {
                encryptedInfoThreadLocal.set(encryptedInfo);
                return true;
            }
        }

        throw new SignatureFailedException("签名错误");

    }
}

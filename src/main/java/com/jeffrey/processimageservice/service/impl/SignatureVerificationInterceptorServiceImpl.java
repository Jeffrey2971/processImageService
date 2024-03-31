package com.jeffrey.processimageservice.service.impl;

import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.mapper.SignatureVerificationInterceptorMapper;
import com.jeffrey.processimageservice.entities.sign.SignatureParams;
import com.jeffrey.processimageservice.service.SignatureVerificationInterceptorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j
public class SignatureVerificationInterceptorServiceImpl implements SignatureVerificationInterceptorService {

    private final SignatureVerificationInterceptorMapper signatureVerificationInterceptorMapper;

    @Autowired
    public SignatureVerificationInterceptorServiceImpl(SignatureVerificationInterceptorMapper signatureVerificationInterceptorMapper) {
        this.signatureVerificationInterceptorMapper = signatureVerificationInterceptorMapper;
    }

    @Override
    public EncryptedInfo signature(SignatureParams signatureParams) {

        String appId = signatureParams.getAppId();
        Integer salt = signatureParams.getSalt();
        byte[] imageBytes = signatureParams.getImageBytes();

        if (
                StringUtils.hasText(appId) && appId.length() == 16 &&
                        String.valueOf(salt).length() == 8 &&
                        imageBytes != null && imageBytes.length > 0
        ) {
            EncryptedInfo encryptedInfo = signatureVerificationInterceptorMapper.getAppSecret(signatureParams.getAppId());

            if (encryptedInfo != null) {

                String imageMd5 = DigestUtils.md5DigestAsHex(signatureParams.getImageBytes());

                encryptedInfo.setImageUniqueIdentification(imageMd5);

                String sign = DigestUtils.md5DigestAsHex((signatureParams.getAppId() + imageMd5 + signatureParams.getSalt() + encryptedInfo.getAppSecret()).getBytes());

                encryptedInfo.setSign(sign);

                return encryptedInfo;
            }
        }
        return null;
    }
}

package com.jeffrey.processimageservice.mapper;

import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface SignatureVerificationInterceptorMapper {
    EncryptedInfo getAppSecret(String appId);
}

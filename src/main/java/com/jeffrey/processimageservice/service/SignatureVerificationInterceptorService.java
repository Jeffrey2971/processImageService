package com.jeffrey.processimageservice.service;

import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.entities.sign.SignatureParams;
import org.springframework.stereotype.Service;

@Service
public interface SignatureVerificationInterceptorService {

    /**
     *
     * @param signatureParams
     * @return
     */
    EncryptedInfo signature(SignatureParams signatureParams);
}

package com.jeffrey.processimageservice.service;

import com.jeffrey.processimageservice.entities.RequestParamsWrapper;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import org.springframework.stereotype.Service;

@Service
public interface ProcessService {

    /**
     * @param requestParamsWrapper
     * @return
     */
    GenericResponse taskAllocation(RequestParamsWrapper requestParamsWrapper, boolean task, EncryptedInfo encryptedInfo, GenericResponse cacheGenericResponse);
}

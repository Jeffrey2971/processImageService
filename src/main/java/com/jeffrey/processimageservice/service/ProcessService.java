package com.jeffrey.processimageservice.service;

import com.jeffrey.processimageservice.entities.RequestParamsWrapper;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.LinkedList;

@Service
public interface ProcessService {

    /**
     *
     * @param requestParamsWrapper
     * @return
     */
    GenericResponse taskAllocation(RequestParamsWrapper requestParamsWrapper, boolean task, EncryptedInfo encryptedInfo, GenericResponse cacheGenericResponse);

    GenericResponse simpleDemoProcess(MultipartFile file, LinkedList<String> rect);
}

package com.jeffrey.processimageservice.service;

import com.jeffrey.processimageservice.entities.ProcessStatus;
import org.springframework.stereotype.Service;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
public interface ProcessStatusService {
    void save(ProcessStatus processStatus);

    Integer selectIdByUserOpenId(String openid);
}

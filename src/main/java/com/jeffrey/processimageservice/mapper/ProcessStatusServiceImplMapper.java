package com.jeffrey.processimageservice.mapper;

import com.jeffrey.processimageservice.entities.ProcessStatus;
import org.springframework.stereotype.Repository;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Repository
public interface ProcessStatusServiceImplMapper {
    void save(ProcessStatus processStatus);

    Integer selectIdByUserOpenId(String openid);
}

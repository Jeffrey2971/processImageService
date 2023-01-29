package com.jeffrey.processimageservice.service.impl;

import com.jeffrey.processimageservice.entities.ProcessStatus;
import com.jeffrey.processimageservice.mapper.ProcessStatusServiceImplMapper;
import com.jeffrey.processimageservice.service.ProcessStatusService;
import org.springframework.stereotype.Component;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class ProcessStatusServiceImpl implements ProcessStatusService {

    private final ProcessStatusServiceImplMapper processStatusServiceImplMapper;

    public ProcessStatusServiceImpl(ProcessStatusServiceImplMapper processStatusServiceImplMapper) {
        this.processStatusServiceImplMapper = processStatusServiceImplMapper;
    }

    @Override
    public void save(ProcessStatus processStatus) {
        processStatusServiceImplMapper.save(processStatus);
    }
}

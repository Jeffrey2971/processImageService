package com.jeffrey.processimageservice.strategy.impl;

import com.jeffrey.processimageservice.enums.ResponseStatus;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.strategy.ShoutNotUpdateCountsStrategy;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class ImageProcessStatusFailed implements ShoutNotUpdateCountsStrategy {
    @Override
    public boolean showNotUpdateCounts(GenericResponse genericResponse) {
        return genericResponse.getData().getProcessStatus() == ResponseStatus.SC_PROCESS_FAILED.getValue();
    }
}

package com.jeffrey.processimageservice.strategy.impl;

import com.jeffrey.processimageservice.entities.enums.ResponseStatus;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.strategy.ShoutNotUpdateCountsStrategy;

/**
 * @author jeffrey
 * @since JDK 1.8
 */
public class ImageProcessStatusHasCached implements ShoutNotUpdateCountsStrategy {

    @Override
    public boolean showNotUpdateCounts(GenericResponse genericResponse) {
        return genericResponse.getHttpCode() == ResponseStatus.SC_NOT_MODIFIED.getValue();
    }

}

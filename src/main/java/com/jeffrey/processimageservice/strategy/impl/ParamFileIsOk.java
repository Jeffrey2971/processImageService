package com.jeffrey.processimageservice.strategy.impl;

import com.jeffrey.processimageservice.entities.DemoRequestParams;
import com.jeffrey.processimageservice.strategy.PublicAccountExperienceAccessStrategy;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class ParamFileIsOk implements PublicAccountExperienceAccessStrategy {
    @Override
    public boolean accessParamsHasNotProblem(DemoRequestParams demoRequestParams) {
        return !demoRequestParams.getFile().isEmpty();
    }
}

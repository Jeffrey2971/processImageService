package com.jeffrey.processimageservice.strategy;

import com.jeffrey.processimageservice.entities.DemoRequestParams;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public interface PublicAccountExperienceAccessStrategy {
    boolean accessParamsHasNotProblem(DemoRequestParams demoRequestParams);
}

package com.jeffrey.processimageservice.strategy.impl;

import com.jeffrey.processimageservice.entities.DemoRequestParams;
import com.jeffrey.processimageservice.strategy.PublicAccountExperienceAccessStrategy;
import org.springframework.util.StringUtils;

import java.util.LinkedList;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class ParamRectIsOk implements PublicAccountExperienceAccessStrategy {
    @Override
    public boolean accessParamsHasNotProblem(DemoRequestParams demoRequestParams) {
        LinkedList<String> rect = demoRequestParams.getRect();

        if (rect == null || rect.size() != 4) {
            return false;
        }

        for (String r : rect) {
            try {
                Integer.parseInt(r);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        return true;

    }
}

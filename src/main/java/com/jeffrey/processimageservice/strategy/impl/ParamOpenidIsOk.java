package com.jeffrey.processimageservice.strategy.impl;

import com.jeffrey.processimageservice.entities.DemoRequestParams;
import com.jeffrey.processimageservice.strategy.PublicAccountExperienceAccessStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 这里只检查 openid 的合法性，不检查是否为公众号用户
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class ParamOpenidIsOk implements PublicAccountExperienceAccessStrategy {


    @Override
    public boolean accessParamsHasNotProblem(DemoRequestParams demoRequestParams) {
        String openid = demoRequestParams.getOpenid();
        return StringUtils.hasText(openid) && openid.length() == 28;
    }
}

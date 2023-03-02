package com.jeffrey.processimageservice.strategy;

import com.jeffrey.processimageservice.entities.DemoRequestParams;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.strategy.impl.*;
import org.springframework.stereotype.Component;

/**
 * 定义策略使复杂的判断更易于维护
 *
 * @author jeffrey
 * @since JDK 1.8
 */
public class StrategyFactory {


    private static final ShoutNotUpdateCountsStrategy[] IMAGE_STRATEGIES = new ShoutNotUpdateCountsStrategy[]{
            new ImageProcessStatusHasCached(),
            new ImageProcessStatusNotSuccess(),
            new ImageProcessStatusNothingContent()
    };

    private static final PublicAccountExperienceAccessStrategy[] PUBLIC_ACCOUNT_STRATEGIES = new PublicAccountExperienceAccessStrategy[]{
            new ParamOpenidIsOk(),
            new ParamRectIsOk(),
            new ParamFileIsOk()
    };

    public static boolean shouldNotModifyCounts(GenericResponse genericResponse) {
        for (ShoutNotUpdateCountsStrategy strategy : IMAGE_STRATEGIES) {
            if (strategy.showNotUpdateCounts(genericResponse)) {
                return true;
            }
        }
        return false;
    }

    public static boolean publicAccountExperienceAccessParamsIsOk(DemoRequestParams demoRequestParams){
        for (PublicAccountExperienceAccessStrategy strategy : PUBLIC_ACCOUNT_STRATEGIES) {
            if (!strategy.accessParamsHasNotProblem(demoRequestParams)) {
                return false;
            }
        }
        return true;
    }
}

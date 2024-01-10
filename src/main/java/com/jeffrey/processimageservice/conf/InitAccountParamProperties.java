package com.jeffrey.processimageservice.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@ConfigurationProperties(prefix = "account")
@Data
public class InitAccountParamProperties {
    private Integer longTermUsageCount;
    private Integer limitedTermUsageCount;
    private Integer limitedTermExpireTimes;
    private Integer callSuccessful;
}

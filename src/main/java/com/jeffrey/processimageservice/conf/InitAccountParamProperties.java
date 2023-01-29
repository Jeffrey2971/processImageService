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
    private Integer apiCanUseCount;
    private Integer apiUsedCount;
}

package com.jeffrey.processimageservice.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@ConfigurationProperties(prefix = "translate")
@Data
public class TranslateProperties {
    private String api;
    private String appId;
    private String appKey;
    private Integer version;
    private String mac;
    private String cuid;
    private Integer paste;
    private String from;
    private String to;
}

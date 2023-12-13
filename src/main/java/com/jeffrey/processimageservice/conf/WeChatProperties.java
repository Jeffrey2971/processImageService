package com.jeffrey.processimageservice.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@ConfigurationProperties(prefix = "wx")
public class WeChatProperties {
    private String appid;
    private String secret;
    private String redirectURI;
    private String responseType;
    private String scope;

}

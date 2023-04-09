package com.jeffrey.processimageservice.entities;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@Component
@ConfigurationProperties(prefix = "info")
public class Info {
    private String serverDomain;
    private String getPublicAccountTokenUrl;
    private String secretKeyLocation;
    private String getUserInfoUrl;
    private String rsaPublicKey;
    private String rsaPrivateKey;
    private CacheConfig cacheConfig;
    private String editDistanceSimilar;

}

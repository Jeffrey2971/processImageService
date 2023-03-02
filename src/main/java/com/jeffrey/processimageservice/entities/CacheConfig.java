package com.jeffrey.processimageservice.entities;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class CacheConfig {
    private TimeUnit timeUnit;
    private Integer expire;
}

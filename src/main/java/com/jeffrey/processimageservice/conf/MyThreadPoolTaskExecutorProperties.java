package com.jeffrey.processimageservice.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@ConfigurationProperties(prefix = "pool")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyThreadPoolTaskExecutorProperties {
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer keepAliveSeconds;
    private Integer queueCapacity;
    private String threadNamePrefix;
    private Boolean waitForTasksToCompleteOnShutdown;
    private Integer awaitTerminationSeconds;
}

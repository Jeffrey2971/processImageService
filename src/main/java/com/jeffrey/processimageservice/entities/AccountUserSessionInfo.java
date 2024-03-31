package com.jeffrey.processimageservice.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountUserSessionInfo {
    private String appId;
    private String appSecret;
    private String username;
    private Integer apiCanUseCount;
    private Integer apiUsedCount;
    private Integer accountId;
}

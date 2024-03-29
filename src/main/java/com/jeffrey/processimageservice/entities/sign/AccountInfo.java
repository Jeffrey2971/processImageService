package com.jeffrey.processimageservice.entities.sign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private Integer longTermUsageCount;
    private Integer limitedTermUsageCount;
    private Integer callSuccessful;
    private LocalDateTime limitedTermExpireTimes;
    private LocalDateTime lastModify;

    public AccountInfo(Integer longTermUsageCount, LocalDateTime lastModify) {
        this.longTermUsageCount = longTermUsageCount;
        this.lastModify = lastModify;
    }

    public AccountInfo(LocalDateTime limitedTermExpireTimes) {
        this.limitedTermExpireTimes = limitedTermExpireTimes;
    }
}

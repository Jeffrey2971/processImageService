package com.jeffrey.processimageservice.entities.sign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Integer apiCanUseCount;
    private Integer apiUsedCount;
    private String lastModifyTime;
}

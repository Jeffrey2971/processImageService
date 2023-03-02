package com.jeffrey.processimageservice.entities.publicAccount;

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
public class InitializationUserInfo {
    private Integer id;
    private String openid;
    private Integer canUse;
    private Integer allUsed;
    private String lastModifyTime;
}

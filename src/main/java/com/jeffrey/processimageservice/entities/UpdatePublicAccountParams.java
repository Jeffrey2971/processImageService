package com.jeffrey.processimageservice.entities;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class UpdatePublicAccountParams {
    private String openid;
    private Integer canUse;
    private Integer allUse;
    private String lastModifyTime;
}

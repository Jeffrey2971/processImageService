package com.jeffrey.processimageservice.entities.sign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 签名所需校验参数
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignatureParams {
    private String appId;
    private Integer salt;
    private byte[] imageBytes;
}

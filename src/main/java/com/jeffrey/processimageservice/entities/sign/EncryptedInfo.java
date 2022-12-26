package com.jeffrey.processimageservice.entities.sign;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedInfo implements Serializable {
    private Integer id;
    private String publicKey;
    private String privateSecret;
    private String sign;
    private String imageUniqueIdentification;
}

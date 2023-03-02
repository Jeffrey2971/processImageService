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
public class CreateMail {
    private String createTime;
    private String targetUsername;
    private String targetLocation;
    private String subject;
    private String content;
    private String code;
}

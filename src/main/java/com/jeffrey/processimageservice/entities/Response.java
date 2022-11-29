package com.jeffrey.processimageservice.entities;

import lombok.*;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@lombok.Data
@AllArgsConstructor
public class Response {
    private Integer httpCode;
    private String httpMsg;
    private String returnType;
    private Integer remainingUsage;
    private Integer allUsedCount;
    private String msgInnerReferer;
    private String location;
    private Data data;
}
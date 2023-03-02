package com.jeffrey.processimageservice.vo;

import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class PageInnerData {
    private Integer id;
    private String status;
    private String message;
    private String sign;
    private Integer accountId;
    private String finishedTime;
}

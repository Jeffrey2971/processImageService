package com.jeffrey.processimageservice.vo;

import lombok.Data;

import java.util.List;

/**
 * 每一页的数据
 *
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class Page {
    private Integer code;
    private String msg;
    private Long count;
    private List<PageInnerData> data;
}

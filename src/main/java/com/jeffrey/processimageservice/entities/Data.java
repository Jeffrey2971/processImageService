package com.jeffrey.processimageservice.entities;

import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


@lombok.Data
@AllArgsConstructor
public class Data {
    private Integer processStatus;
    private Integer processCount;
    private String processMsg;
    private List<Point> processLocation;
    private String url;
}

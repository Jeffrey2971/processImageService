package com.jeffrey.processimageservice.entities.response;

import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


@lombok.Data
@AllArgsConstructor
public class Data implements Serializable {
    private Integer processStatus;
    private Integer processCount;
    private String processMsg;
    private List<Point> processLocation;
    private String url;
}

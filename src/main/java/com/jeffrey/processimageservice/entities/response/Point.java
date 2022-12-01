package com.jeffrey.processimageservice.entities.response;

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
public class Point {
    private Integer x;
    private Integer y;
    private Integer w;
    private Integer h;
    private Integer lineCount;
    private String word;
}

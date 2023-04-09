package com.jeffrey.processimageservice.model;

import com.jeffrey.processimageservice.enums.MarkType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericMark {
    private String value;
    private Enum<MarkType> rule;
}

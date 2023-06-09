package com.jeffrey.processimageservice.entities;

import com.jeffrey.processimageservice.enums.ProcessStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProcessStatus {
    private Integer id;
    private Enum<ProcessStatusEnum> status;
    private String message;
    private String sign;
    private String createTime;
    private Integer aid;
}

package com.jeffrey.processimageservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Getter
@AllArgsConstructor
public enum Product {
    WATERMARK_PROCESS_01("watermark-process-01"),
    WATERMARK_PROCESS_02("watermark-process-02"),
    WATERMARK_PROCESS_03("watermark-process-03"),
    WATERMARK_PROCESS_04("watermark-process-04"),
    WATERMARK_PROCESS_05("watermark-process-05"),
    WATERMARK_PROCESS_06("watermark-process-06"),
    WATERMARK_PROCESS_07("watermark-process-07"),
    WATERMARK_PROCESS_08("watermark-process-08"),
    WATERMARK_PROCESS_MONTHLY_01("watermark-process-monthly-01"),
    WATERMARK_PROCESS_MONTHLY_02("watermark-process-monthly-02"),
    WATERMARK_PROCESS_MONTHLY_03("watermark-process-monthly-03"),
    WATERMARK_PROCESS_MONTHLY_04("watermark-process-monthly-04"),
    WATERMARK_PROCESS_MONTHLY_05("watermark-process-monthly-05"),
    WATERMARK_PROCESS_MONTHLY_06("watermark-process-monthly-06"),
    WATERMARK_PROCESS_MONTHLY_07("watermark-process-monthly-07"),
    WATERMARK_PROCESS_MONTHLY_08("watermark-process-monthly-08");


    private final String type;
}

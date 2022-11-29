package com.jeffrey.processimageservice.entities;

import com.jeffrey.processimageservice.entities.enums.SupportUploadImageType;
import com.jeffrey.processimageservice.entities.enums.UploadMethodType;
import lombok.Data;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class RequestParamsWrapper {

    private RequestParams requestParams;

    private Enum<UploadMethodType> uploadMethodTypeEnum;

    private Enum<SupportUploadImageType> supportUploadImageTypeEnum;

    // 使用 byte 数组，避免该流被提前消费
    private byte[] finalImageBytes;

}

package com.jeffrey.processimageservice.entities.response;

import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse implements Serializable {

    public GenericResponse(Integer httpCode, String httpMsg, String returnType, Integer remainingUsage, Integer callSuccessful, String msgInnerReferer, String location, Data data) {
        this.httpCode = httpCode;
        this.httpMsg = httpMsg;
        this.returnType = returnType;
        this.remainingUsage = remainingUsage;
        this.callSuccessful = callSuccessful;
        this.msgInnerReferer = msgInnerReferer;
        this.location = location;
        this.data = data;
    }

    private Integer httpCode;
    private String httpMsg;
    private String returnType;
    private Integer remainingUsage;
    private Integer longTermUsageCount;
    private Integer limitedTermUsageCount;
    private LocalDateTime limitedTermExpireTimes;
    private Integer callSuccessful;
    private String msgInnerReferer;
    private String location;
    private Data data;

    private EncryptedInfo encryptedInfo;
}
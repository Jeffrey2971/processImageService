package com.jeffrey.processimageservice.entities;

import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
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
public class AsyncPendingTasksItem {
    private long taskCreateTimeStamp;
    private long taskFinishedTimeStamp;
    private RequestParamsWrapper requestParamsWrapper;
    private Boolean isSuccessful;
    private EncryptedInfo encryptedInfo;
}

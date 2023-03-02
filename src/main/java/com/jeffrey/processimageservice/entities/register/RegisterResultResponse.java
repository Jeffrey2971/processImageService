package com.jeffrey.processimageservice.entities.register;

import com.jeffrey.processimageservice.entities.FailedItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResultResponse {
    private Integer httpCode;
    private String httpMsg;
    private List<FailedItem> failedItems;
}
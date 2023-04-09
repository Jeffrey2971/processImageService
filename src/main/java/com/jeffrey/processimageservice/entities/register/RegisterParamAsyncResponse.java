package com.jeffrey.processimageservice.entities.register;

import com.jeffrey.processimageservice.enums.LoginStatus;
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
public class RegisterParamAsyncResponse {
    private Enum<LoginStatus> status;
    private String msg;
}

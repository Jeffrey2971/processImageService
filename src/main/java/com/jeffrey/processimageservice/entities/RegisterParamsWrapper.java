package com.jeffrey.processimageservice.entities;

import com.jeffrey.processimageservice.conf.InitAccountParamProperties;
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
public class RegisterParamsWrapper {
    private RegisterParams registerParams;
    private InitAccountParamProperties initAccountParams;
    private String creationTime;
    private Integer id;
}

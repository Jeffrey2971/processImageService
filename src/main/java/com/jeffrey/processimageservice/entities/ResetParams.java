package com.jeffrey.processimageservice.entities;

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
public class ResetParams {
    private String newPassword;
    private String currentPassword;
    private String uniqueToken;
}

package com.jeffrey.processimageservice.entities;

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
public class RegisterParams {
    private String username;
    private String password;
    private String ensure;
    private String email;
}

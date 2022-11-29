package com.jeffrey.processimageservice.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private Integer apiCanUseCount;
    private Integer apiUsedCount;
    private String lastModifyTime;
}

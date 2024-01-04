package com.jeffrey.processimageservice.entities;

import com.jeffrey.processimageservice.enums.ProductType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductInfo {
    private Integer id;
    private String pid;
    private ProductType type;
    private String title;
    private Integer price;
    private Integer total;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

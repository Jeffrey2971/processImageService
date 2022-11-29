package com.jeffrey.processimageservice.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestParams {

    // 基本参数
    private Object watermarkName;

    // String -> List<Point>
    private Object rectangles;

    // String -> List<String>
    private Object excludeKeywords;

    // String -> Boolean
    private Object sync;
    private Object offset;
    private String callback;

    // 文件上传
    private String imageUrl;
    private MultipartFile uploadFile;
    private String imageBase64;
}

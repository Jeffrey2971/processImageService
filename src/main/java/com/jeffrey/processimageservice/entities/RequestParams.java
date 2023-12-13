package com.jeffrey.processimageservice.entities;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestParams implements Cloneable {

    // 基本参数
    private Object watermarkName;

    // String -> List<Point>
    private Object rectangles;
    private Object originRectangles;

    // String -> List<String>
    private Object excludeKeywords;

    // String -> Boolean
    private Object sync;
    private Object offset;
    private String callback;
    private String imageUrl;
    private MultipartFile uploadFile;
    private String imageBase64;

    // String -> Boolean
    private Object showOnly;

    private Object ocrOnly;

    @Override
    public String toString() {
        return "RequestParams{" +
                "watermarkName=" + watermarkName +
                ", rectangles=" + rectangles +
                ", originRectangles=" + originRectangles +
                ", excludeKeywords=" + excludeKeywords +
                ", sync=" + sync +
                ", offset=" + offset +
                ", callback='" + callback + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", uploadFile=" + uploadFile +
                ", imageBase64='" + imageBase64 + '\'' +
                ", showOnly=" + showOnly +
                ", ocrOnly=" + ocrOnly +
                '}';
    }

    public String signatureParamsToString() {
        return "RequestParams{" +
                "watermarkName=" + watermarkName +
                ", rectangles=" + rectangles +
                ", originRectangles=" + originRectangles +
                ", excludeKeywords=" + excludeKeywords +
                ", sync=" + sync +
                ", offset=" + offset +
                ", callback='" + callback + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageBase64='" + imageBase64 + '\'' +
                ", showOnly=" + showOnly +
                ", ocrOnly=" + ocrOnly +
                '}';
    }

    @Override
    public RequestParams clone() {
        try {
            return (RequestParams) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}

package com.jeffrey.processimageservice.utils;

import com.jeffrey.processimageservice.enums.SupportUploadImageType;
import org.apache.tika.Tika;

import java.io.IOException;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class FileUtil {

    /**
     * 校验给定的 bytes 数组是否为图片且格式为 image/bmp image/jpeg image/png
     * @param bytes 图片的字节数组
     * @return 为 null 则表示不支持
     * @throws IOException ioException
     */
    public static SupportUploadImageType getSupportUploadImageType(byte[] bytes) throws IOException {

        String type = new Tika().detect(bytes);

        return "image/png".equals(type)
                ? SupportUploadImageType.IMAGE_PNG : "image/bmp".equals(type)
                ? SupportUploadImageType.IMAGE_BMP : "image/jpeg".equals(type)
                ? SupportUploadImageType.IMAGE_JPEG : null;
    }

}

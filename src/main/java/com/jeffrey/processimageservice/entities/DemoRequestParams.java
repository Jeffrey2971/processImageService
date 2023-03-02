package com.jeffrey.processimageservice.entities;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Data
public class DemoRequestParams {

    private MultipartFile file;
    private LinkedList<String> rect;
    private String openid;
}

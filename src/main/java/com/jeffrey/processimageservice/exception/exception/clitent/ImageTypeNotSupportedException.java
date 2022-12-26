package com.jeffrey.processimageservice.exception.exception.clitent;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class ImageTypeNotSupportedException extends RuntimeException{

    private static final long serialVersionUID = -7034468190745722939L;

    public ImageTypeNotSupportedException(String message) {
        super(message);
    }

}

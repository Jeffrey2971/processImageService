package com.jeffrey.processimageservice.exception.exception.clitent;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class FileTooLargeException extends RuntimeException{

    private static final long serialVersionUID = -7034897190745722939L;

    public FileTooLargeException(String message) {
        super(message);
    }

}

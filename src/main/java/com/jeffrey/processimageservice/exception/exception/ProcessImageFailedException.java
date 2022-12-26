package com.jeffrey.processimageservice.exception.exception;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class ProcessImageFailedException extends RuntimeException{

    private static final long serialVersionUID = -7034468108675722939L;
    public ProcessImageFailedException(String message) {
        super(message);
    }
}

package com.jeffrey.processimageservice.exception.exception;

/**
 * @author jeffrey
 * @since JDK 1.8
 */
public class ArgumentsOverwriteException extends RuntimeException{

    private static final long serialVersionUID = -7034897190745766939L;

    public ArgumentsOverwriteException(String message) {
        super(message);
    }
}

package com.jeffrey.processimageservice.exception.exception;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class UnknownArgumentException extends RuntimeException{

    private static final long serialVersionUID = -7034468190708792939L;

    public UnknownArgumentException(String message) {
        super(message);
    }

}

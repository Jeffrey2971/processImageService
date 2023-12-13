package com.jeffrey.processimageservice.exception.exception;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class OrderException extends RuntimeException{
    static final long serialVersionUID = -1234567898765432124L;

    public OrderException(String message) {
        super(message);
    }

    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }
}

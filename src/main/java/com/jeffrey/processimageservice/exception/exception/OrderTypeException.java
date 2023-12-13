package com.jeffrey.processimageservice.exception.exception;


/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class OrderTypeException extends OrderException {
    private String type;
    public OrderTypeException(String message) {
        super(message);
    }

    public OrderTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderTypeException(String message, String type) {
        super(message);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

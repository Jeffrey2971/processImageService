package com.jeffrey.processimageservice.exception.exception;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class PaymentException extends RuntimeException{
    static final long serialVersionUID = -1234567465492612124L;

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}

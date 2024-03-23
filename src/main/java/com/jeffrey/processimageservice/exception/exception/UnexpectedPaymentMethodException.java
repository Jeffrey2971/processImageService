package com.jeffrey.processimageservice.exception.exception;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class UnexpectedPaymentMethodException extends PaymentException{
    public UnexpectedPaymentMethodException(String message) {
        super(message);
    }

    public UnexpectedPaymentMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}

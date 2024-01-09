package com.jeffrey.processimageservice.exception.exception.clitent;

import com.jeffrey.processimageservice.exception.exception.OrderException;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class InvalidPaymentTypeException extends OrderException {
    public InvalidPaymentTypeException(String message) {
        super(message);
    }

    public InvalidPaymentTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}

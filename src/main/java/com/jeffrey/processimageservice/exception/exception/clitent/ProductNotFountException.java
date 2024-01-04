package com.jeffrey.processimageservice.exception.exception.clitent;

import com.jeffrey.processimageservice.exception.exception.OrderException;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class ProductNotFountException extends OrderException {

    public ProductNotFountException(String message) {
        super(message);
    }

    public ProductNotFountException(String message, Throwable cause) {
        super(message, cause);
    }
}

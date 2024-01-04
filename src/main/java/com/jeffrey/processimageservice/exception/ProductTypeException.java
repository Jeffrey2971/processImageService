package com.jeffrey.processimageservice.exception;

import com.jeffrey.processimageservice.exception.exception.ProductException;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class ProductTypeException extends ProductException {
    public ProductTypeException(String message) {
        super(message);
    }

    public ProductTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}

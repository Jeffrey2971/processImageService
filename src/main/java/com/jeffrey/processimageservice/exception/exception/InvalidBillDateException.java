package com.jeffrey.processimageservice.exception.exception;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class InvalidBillDateException extends OrderException {
    private String date;
    public InvalidBillDateException(String message) {
        super(message);
    }

    public InvalidBillDateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBillDateException(String message, String date) {
        super(message);
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}

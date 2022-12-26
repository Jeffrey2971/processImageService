package com.jeffrey.processimageservice.exception.exception.clitent;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class AccountException extends RuntimeException{

    private static final long serialVersionUID = -7034897213745766939L;


    public AccountException() {
    }

    public AccountException(String message) {
        super(message);
    }

    public AccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountException(Throwable cause) {
        super(cause);
    }

    public AccountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

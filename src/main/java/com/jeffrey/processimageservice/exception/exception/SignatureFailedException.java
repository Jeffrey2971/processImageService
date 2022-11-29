package com.jeffrey.processimageservice.exception.exception;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class SignatureFailedException extends RuntimeException{
    private static final long serialVersionUID = -7034468108675715939L;

    public SignatureFailedException() {
    }

    public SignatureFailedException(String message) {
        super(message);
    }

    public SignatureFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignatureFailedException(Throwable cause) {
        super(cause);
    }

    public SignatureFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

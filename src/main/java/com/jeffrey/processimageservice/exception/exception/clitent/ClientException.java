package com.jeffrey.processimageservice.exception.exception.clitent;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class ClientException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private String message;

    public ClientException(Throwable cause) {
        super(cause);
    }

    public ClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ClientException(String message) {
        this.message = message;
    }

    public ClientException(){
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return message;
    }
}

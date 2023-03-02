package com.jeffrey.processimageservice.exception.exception.server;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class ServiceShuttingDownException extends RuntimeException {
    private static final long serialVersionUID = -7034468190706201239L;
    public ServiceShuttingDownException() {
    }

    public ServiceShuttingDownException(String message) {
        super(message);
    }
}

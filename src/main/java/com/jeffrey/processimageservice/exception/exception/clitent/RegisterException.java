package com.jeffrey.processimageservice.exception.exception.clitent;

import com.jeffrey.processimageservice.entities.FailedItem;

import java.util.List;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class RegisterException extends ClientException{

    private static final long serialVersionUID = -6524468108675722939L;

    private List<FailedItem> failedItems;

    public List<FailedItem> getFailedItems() {
        return failedItems;
    }

    public void setFailedItems(List<FailedItem> failedItems) {
        this.failedItems = failedItems;
    }

    public RegisterException(String message) {
        super(message);
    }
}

package com.jeffrey.processimageservice.enums;

/**
 * @author jeffrey
 */
public enum AccountStatus {

    /**
     * {@code 0 ZERO_USED}
     * <p>
     * 账户可使用次数不足
     */
    ZERO_USED(0);

    private final int value;

    public int getValue() {
        return value;
    }

    private AccountStatus(int value) {
        this.value = value;
    }
}

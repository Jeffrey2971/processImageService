package com.jeffrey.processimageservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum WxPaymentType {
    NATIVE("NATIVE"),
    JSAPI("JSAPI");

    private String type;
}

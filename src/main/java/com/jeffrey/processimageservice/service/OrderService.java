package com.jeffrey.processimageservice.service;


import com.jeffrey.processimageservice.vo.R;

public interface OrderService {
//    String createQrCodeToBase64(String content, int width, int height);
    String getOpenId(String code, String state);
    String getOpenId();

    String getUserIdentifier();

    boolean hasOpenIdInSession();

    R processOrder(String orderNo);

}

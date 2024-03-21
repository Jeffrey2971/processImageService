package com.jeffrey.processimageservice.service;


import com.jeffrey.processimageservice.entities.OrderInfo;
import com.jeffrey.processimageservice.enums.OrderStatus;
import com.jeffrey.processimageservice.enums.Product;
import com.jeffrey.processimageservice.enums.WxPaymentType;
import com.jeffrey.processimageservice.vo.R;

public interface OrderService {
    String getOpenId(String code, String state);

    String getOpenId();

    String getUserIdentifier();

    boolean hasOpenIdInSession();

    R processOrder(String orderNo, OrderInfo ignore);

    OrderInfo queryLocalOrder(String id, String orderNo);

    R createAndPrepayOrder(Product productPID, WxPaymentType type);

    void updateLocalOrderStatus(String orderNo, OrderStatus orderStatus);
}

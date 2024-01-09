package com.jeffrey.processimageservice.service;


import com.jeffrey.processimageservice.entities.OrderInfo;
import com.jeffrey.processimageservice.enums.OrderStatus;
import com.jeffrey.processimageservice.vo.R;

public interface OrderService {
    String getOpenId(String code, String state);

    String getOpenId();

    String getUserIdentifier();

    boolean hasOpenIdInSession();

    R processOrder(String orderNo, OrderInfo ignore);

    OrderInfo queryLocalOrder(String id, String orderNo);

    R createAndPrepayOrder(String id, String type);

    void updateLocalOrderStatus(String orderNo, OrderStatus orderStatus);
}

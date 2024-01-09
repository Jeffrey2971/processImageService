package com.jeffrey.processimageservice.mapper;

import com.jeffrey.processimageservice.entities.OrderInfo;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface OrderServiceImplMapper {
    OrderInfo queryLocalOrder(String uid, String orderNo);

    void saveOrder1(Integer uid, String pid, String title, String orderNo, String orderStatus, String orderType);
    void saveOrder2(OrderInfo orderInfo);

    void updateLocalOrderStatus(String orderStatus,LocalDateTime updateTime,String uid,String orderNo);
}

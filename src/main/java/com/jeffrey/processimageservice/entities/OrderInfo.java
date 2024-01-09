package com.jeffrey.processimageservice.entities;

import com.jeffrey.processimageservice.enums.WxPaymentType;
import lombok.*;

@Data
public class OrderInfo {

    // 订单标题
    private String title;

    // 商户订单编号
    private String orderNo;

    // 用户id
    private String userId;

    // 支付产品id
    private String productId;
    // 支付产品pid
    private String productPid;

    // 支付类型
    private WxPaymentType paymentType;

    // 订单金额(分)
    private Integer totalFee;

    // 订单二维码连接
    private String paymentId;

    // 订单状态
    private String orderStatus;
    private String orderType;

    private String id; //主键

    private String createTime;//创建时间

    private String updateTime;//更新时间
}
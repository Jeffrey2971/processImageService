package com.jeffrey.processimageservice.aop.impl.order;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.jeffrey.processimageservice.entities.OrderInfo;
import com.jeffrey.processimageservice.entities.sign.AccountInfo;
import com.jeffrey.processimageservice.enums.OrderStatus;
import com.jeffrey.processimageservice.service.OrderService;
import com.jeffrey.processimageservice.utils.PayUtil;
import com.jeffrey.processimageservice.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Slf4j
@Aspect
@Component
public class UpdateOrderAOP {
    private final OrderService orderService;

    @Autowired
    public UpdateOrderAOP(OrderService orderService) {
        this.orderService = orderService;
    }

    @Pointcut(value = "@annotation(com.jeffrey.processimageservice.aop.annotation.order.Order)")
    public void pointcut() {
    }


    @Around(value = "pointcut()")
    public Object checkOrder(ProceedingJoinPoint pjp) {

        // ################### 核实订单是否有效 ################### //
        if (pjp.getArgs()[0] == null) {
            return R.error().setMessage("订单号为空");
        }
        String reqOrderNo = (String) pjp.getArgs()[0];

        // 查询订单
        OrderInfo orderInfo = PayUtil.queryOrderInfo(reqOrderNo, StpUtil.getLoginIdAsString());

        if (orderInfo == null) {
            return R.error().setMessage("当前账户查询不到相关订单");
        }

        // 校验订单状态
        if (!OrderStatus.SUCCESS.getType().equalsIgnoreCase(orderInfo.getOrderStatus())) {
            return R.error().setMessage(orderInfo.getOrderStatus());
        }
        // ##################### 确定支付安全完成，开始处理订单 ##################### //

        R r;
        try {
            // 更新本地订单状态为已支付

            r = (R) pjp.proceed(new Object[]{reqOrderNo, orderInfo});
        } catch (Throwable e) {
            e.printStackTrace();
            return R.error().setMessage("订单处理失败，请联系管理员");
        }

        if (r.getCode() != 0 || !"完成".equals(r.getMessage())) {
            return R.error().setMessage("订单处理失败，请联系管理员");
        }

        // ##################### 支付完成，更新订单状态 ##################### //
        R updateOrderStatus = PayUtil.updateOrderStatus(reqOrderNo, OrderStatus.FINISHED);
        orderService.updateLocalOrderStatus(orderInfo.getOrderNo(), OrderStatus.FINISHED);
        log.info("更新订单结果：{}", updateOrderStatus.toString());

        // ##################### 更新页面数据 ##################### // 

        SaSession session = StpUtil.getSession();
        AccountInfo accountInfo = (AccountInfo) r.getData().get("updateWrap");

        session.set("apiCanUseCount", accountInfo.getLongTermUsageCount() + accountInfo.getLimitedTermUsageCount());
        session.set("apiUsedCount", accountInfo.getCallSuccessful());
        session.set("limitedTermUsageCount", accountInfo.getLimitedTermUsageCount());
        session.set("longTermUsageCount", accountInfo.getLongTermUsageCount());
        session.set("limitedTermExpireTimes", accountInfo.getLimitedTermExpireTimes());


        return r;
    }

}

package com.jeffrey.processimageservice.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.jeffrey.processimageservice.service.OrderService;
import com.jeffrey.processimageservice.utils.PayUtil;
import com.jeffrey.processimageservice.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Controller
@Slf4j
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/hasid")
    @ResponseBody
    public R hasOpenIdInSession() {
        return R.ok().data("hasId", orderService.hasOpenIdInSession());
    }

    @GetMapping("/callback")
    public String wxCallback(@RequestParam String code, @RequestParam String state) {
        log.info("接收到微信重定向请求，接收获取 openid 所需参数：code = {} ，重定向携带参数：state = {}", code, state);
        orderService.getOpenId(code, state);
        return "redirect:".concat(((String) StpUtil.getSession().get("state")));
    }

    @GetMapping("/topay")
    public ModelAndView jumpPaymentPage() {
        return new ModelAndView("APIPrice");
    }

    @GetMapping("/process-order/{id}/{type}")
    public ModelAndView processOrder(@PathVariable String id, @PathVariable String type) {

        R r = new R();
        ModelAndView mav = new ModelAndView();

        mav.setViewName("payment");

        if ("native".equalsIgnoreCase(type)) {
            r = PayUtil.nativePay(id, orderService.getUserIdentifier());
        }

        if ("jsapi".equalsIgnoreCase(type)) {
            // 查看会话中是否有 openid 如果没有通知客户端先进行重定向获取
            boolean hasId = (boolean) hasOpenIdInSession().getData().get("hasId");
            if (!hasId) {
                r.setCode(401).setMessage("FAILED::MISSING OPENID");
            }else {
                R prepay = PayUtil.jsapiPrePay(id, orderService.getUserIdentifier(), orderService.getOpenId());
                String prepayId = (String)prepay.getData().get("prepayId");
                String orderNo = (String) prepay.getData().get("orderNo");
                if (prepay.getCode() != 0 || StringUtils.isBlank(prepayId)) {
                    r.setCode(500).setMessage("FAILED::CREATE ORDER FAILED");
                }else{
                    r = PayUtil.jsapiPay(prepayId);
                    r.data("orderNo", orderNo);
                    mav.addObject("callback", true);
                }
            }
        }

        mav.addObject("r", r);
        return mav;
    }

    @GetMapping("/query-order-status/{orderNo}")
    @ResponseBody
    public R queryOrderStatus(@PathVariable String orderNo) {
        return PayUtil.queryOrderStatus(orderNo, orderService.getUserIdentifier());
    }

    @GetMapping("/process/{orderNo}")
    @ResponseBody
    public R process(@PathVariable String orderNo){
        return orderService.processOrder(orderNo);
    }
}

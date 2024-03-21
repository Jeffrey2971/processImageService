package com.jeffrey.processimageservice.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.jeffrey.processimageservice.enums.Product;
import com.jeffrey.processimageservice.enums.WxPaymentType;
import com.jeffrey.processimageservice.service.OrderService;
import com.jeffrey.processimageservice.utils.PayUtil;
import com.jeffrey.processimageservice.vo.R;
import lombok.extern.slf4j.Slf4j;
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


    @GetMapping("/process-order/{productPID}/{paymentType}")
    public ModelAndView processOrder(@PathVariable String productPID, @PathVariable String paymentType) {

        Product product;
        WxPaymentType wxPaymentType;
        ModelAndView mav = new ModelAndView();

        try {
            product = Product.valueOf(productPID.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("商品不存在");
        }

        try {
            wxPaymentType = WxPaymentType.valueOf(paymentType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("意外的付款方式");
        }


        R r = orderService.createAndPrepayOrder(product, wxPaymentType);

        if (r.getCode() == 0) {
            mav.addObject("callback", true);
        }

        mav.setViewName("payment");
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
    public R process(@PathVariable String orderNo) {

        return orderService.processOrder(orderNo, null);
    }
}

package com.jeffrey.processimageservice.controller;

import cn.dev33.satoken.stp.StpUtil;
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


    @GetMapping("/process-order/{id}/{type}")
    public ModelAndView processOrder(@PathVariable String id, @PathVariable String type) {

        ModelAndView mav = new ModelAndView();

        R r = orderService.createAndPrepayOrder(id, type);

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

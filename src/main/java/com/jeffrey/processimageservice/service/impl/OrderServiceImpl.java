package com.jeffrey.processimageservice.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jeffrey.processimageservice.aop.annotation.order.Order;
import com.jeffrey.processimageservice.conf.WeChatProperties;
import com.jeffrey.processimageservice.entities.OrderInfo;
import com.jeffrey.processimageservice.entities.ProductInfo;
import com.jeffrey.processimageservice.entities.sign.AccountInfo;
import com.jeffrey.processimageservice.enums.OrderStatus;
import com.jeffrey.processimageservice.enums.ProductType;
import com.jeffrey.processimageservice.exception.ProductTypeException;
import com.jeffrey.processimageservice.exception.exception.CreateOrderException;
import com.jeffrey.processimageservice.exception.exception.clitent.InvalidPaymentTypeException;
import com.jeffrey.processimageservice.exception.exception.clitent.ProductNotFountException;
import com.jeffrey.processimageservice.mapper.OrderServiceImplMapper;
import com.jeffrey.processimageservice.service.AccountService;
import com.jeffrey.processimageservice.service.LoginService;
import com.jeffrey.processimageservice.service.OrderService;
import com.jeffrey.processimageservice.service.ProductService;
import com.jeffrey.processimageservice.utils.PayUtil;
import com.jeffrey.processimageservice.vo.R;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final WeChatProperties weChatProperties;
    private final LoginService loginService;
    private final OrderServiceImplMapper orderServiceMapper;
    private final ProductService productService;
    private final AccountService accountService;

    @Autowired
    public OrderServiceImpl(WeChatProperties weChatProperties, LoginService loginService, OrderServiceImplMapper orderServiceMapper, ProductService productService, AccountService accountService) {
        this.weChatProperties = weChatProperties;
        this.loginService = loginService;
        this.orderServiceMapper = orderServiceMapper;
        this.productService = productService;
        this.accountService = accountService;
    }


    @Override
    public String getOpenId(String code, String state) {

        SaSession session = StpUtil.getSession();

        String openid;
        log.info("开始获取用户 openid");

        if (session.has("openid")) {
            openid = (String) session.get("openid");
            return openid;
        }

        Request req = new Request.Builder()
                .url(
                        "https://api.weixin.qq.com/sns/oauth2/access_token" +
                                "?appid=" + weChatProperties.getAppid() +
                                "&secret=" + weChatProperties.getSecret() +
                                "&code=" + code +
                                "&grant_type=authorization_code")
                .get()
                .build();

        try (Response response = new OkHttpClient().newCall(req).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JsonObject root = new Gson().fromJson(response.body().string(), JsonElement.class).getAsJsonObject();
                if (root.has("openid")) {
                    openid = root.get("openid").getAsString();
                    session.set("openid", openid);
                    session.set("state", state);
                    return openid;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException("获取 openid 失败");
    }

    @Override
    public String getOpenId() {
        return this.getOpenId(null, null);
    }

    @Override
    public String getUserIdentifier() {
        return StpUtil.getLoginIdAsString();
    }

    @Override
    public boolean hasOpenIdInSession() {
        return loginService.hasOpenIdInSession();
    }


    @Order
    @Override
    public R processOrder(String orderNo, OrderInfo orderInfo) {

        log.info("开始处理订单：{}", orderNo);

        String productPid = orderInfo.getProductPid();

        ProductInfo productInfo = productService.queryProductByProductPID(productPid);

        if (productInfo == null) {
            throw new ProductNotFountException("订单商品不存在");
        }

        AccountInfo accountInfo;
        if (ProductType.DEMAND.name().equalsIgnoreCase(productInfo.getType().name())) {

            Integer total = productInfo.getTotal();

            accountInfo = accountService.getAccountInfoById(StpUtil.getLoginIdAsInt());

            accountInfo.setLongTermUsageCount(accountInfo.getLongTermUsageCount() + total);
            accountInfo.setLastModify(LocalDateTime.now());

            accountService.updateAccountInfo(accountInfo);

        } else if (ProductType.MONTHLY.name().equalsIgnoreCase(productInfo.getType().name())) {
            Integer total = productInfo.getTotal();
            accountInfo = accountService.getAccountInfoById(StpUtil.getLoginIdAsInt());
            accountInfo.setLimitedTermUsageCount(accountInfo.getLimitedTermUsageCount() + total);
            accountInfo.setLimitedTermExpireTimes(accountInfo.getLimitedTermExpireTimes() != null
                    ? accountInfo.getLimitedTermExpireTimes().plusDays(30) : LocalDateTime.now().plusDays(30)
            );
            accountInfo.setLastModify(LocalDateTime.now());

            accountService.updateAccountInfo(accountInfo);
        } else {
            throw new ProductTypeException("订单类型不正确");
        }

        return R.ok().setMessage("完成").data("updateWrap", accountInfo);
    }

    @Override
    public OrderInfo queryLocalOrder(String id, String orderNo) {
        return orderServiceMapper.queryLocalOrder(StpUtil.getLoginIdAsString(), orderNo);
    }

    @Override
    public R createAndPrepayOrder(String id, String type) {

        if ("native".equalsIgnoreCase(type)) {
            R r = PayUtil.nativePay(id, this.getUserIdentifier());
            this.createAndRecordOrder(id, type, r);
            if (r.getCode() != 0) {
                throw new CreateOrderException("创建订单失败");
            }
            return r;
        }

        if ("jsapi".equalsIgnoreCase(type)) {
            // 查看会话中是否有 openid 如果没有通知客户端先进行重定向获取
            boolean hasId = hasOpenIdInSession();
            if (!hasId) {
                // 正常的响应不抛异常
                return new R().setCode(401).setMessage("FAILED::MISSING OPENID");
            } else {
                R prepay = PayUtil.jsapiPrePay(id, getUserIdentifier(), getOpenId());
                String prepayId = (String) prepay.getData().get("prepayId");
                String orderNo = (String) prepay.getData().get("orderNo");
                if (prepay.getCode() != 0 || StringUtils.isBlank(prepayId)) {
                    throw new CreateOrderException("创建订单失败");
                } else {
                    R r = PayUtil.jsapiPay(prepayId).data("orderNo", orderNo);
                    this.createAndRecordOrder(id, type, r);
                    return r;
                }
            }
        }
        throw new InvalidPaymentTypeException("支付方式不受支持");
    }

    @Override
    public void updateLocalOrderStatus(String orderNo, OrderStatus orderStatus) {
        orderServiceMapper.updateLocalOrderStatus(orderStatus.getType(), LocalDateTime.now(), StpUtil.getLoginIdAsString(), orderNo);
    }

    //
    private void createAndRecordOrder(String id, String type, R r) {

        Map<String, Object> data = r.getData();
        String orderNo = (String) data.get("orderNo");
        String createTime = (String) data.get("createTime");

        // 查询缓存订单
        OrderInfo orderInfo = orderServiceMapper.queryLocalOrder(this.getUserIdentifier(), orderNo);

        // 判断是否存在缓存订单，需符合订单不为空、订单商品 pid 一致、下单类型一致
        if (orderInfo != null) {
            log.info("存在缓存订单");
            return;
        }

        // 查询商品是否存在
        ProductInfo productInfo = productService.queryProductByProductPID(id);
        if (productInfo == null) {
            log.warn("商品不存在：{}", id);
            throw new ProductNotFountException("商品不存在：" + id);
        }

        // 创建订单
        orderInfo = new OrderInfo();
        orderInfo.setUserId(StpUtil.getLoginIdAsString());
        orderInfo.setProductPid(productInfo.getPid());
        orderInfo.setTitle(productInfo.getTitle());
        orderInfo.setOrderNo((orderNo));
        orderInfo.setOrderStatus(OrderStatus.NOTPAY.getType());
        orderInfo.setOrderType(type);
        orderInfo.setCreateTime(createTime);

        // 缓存订单
        orderServiceMapper.saveOrder2(orderInfo);

    }
}

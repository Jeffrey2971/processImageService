package com.jeffrey.processimageservice.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jeffrey.processimageservice.conf.WeChatProperties;
import com.jeffrey.processimageservice.service.LoginService;
import com.jeffrey.processimageservice.service.OrderService;
import com.jeffrey.processimageservice.utils.PayUtil;
import com.jeffrey.processimageservice.vo.R;
import lombok.extern.slf4j.Slf4j;
//import net.glxn.qrgen.QRCode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final WeChatProperties weChatProperties;
    private final LoginService loginService;

    @Autowired
    public OrderServiceImpl(WeChatProperties weChatProperties, LoginService loginService) {
        this.weChatProperties = weChatProperties;
        this.loginService = loginService;
    }

//    @Override
//    public String createQrCodeToBase64(String content, int width, int height) {
//        ByteArrayOutputStream stream = QRCode.from(content)
//                .withSize(width, height)
//                .stream();
//
//        return "data:image/png;base64," + Base64Utils.encodeToString(stream.toByteArray());
//    }

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

    @Override
    public R processOrder(String orderNo) {
        log.info("开始处理订单：{}", orderNo);
        return PayUtil.queryOrderStatus(orderNo, StpUtil.getLoginIdAsString());
    }
}

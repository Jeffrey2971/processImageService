package com.jeffrey.processimageservice.utils;

import com.google.gson.Gson;

import com.google.gson.annotations.SerializedName;
import com.jeffrey.processimageservice.entities.OrderInfo;
import com.jeffrey.processimageservice.enums.OrderStatus;
import com.jeffrey.processimageservice.exception.exception.InvalidBillDateException;
import com.jeffrey.processimageservice.exception.exception.OrderException;
import com.jeffrey.processimageservice.exception.exception.OrderTypeException;
import com.jeffrey.processimageservice.vo.R;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


@Slf4j
public class PayUtil {
    private static OkHttpClient AUTHORIZATION_CLIENT;
    private static final String DOMAIN = "https://www.processimage.cn";

    // ###################################################### 凭证 ######################################################
    private static final String AUTHORIZED_GRANT_TYPES = "client_credentials";
    private static final String OAUTH_TOKEN = DOMAIN + "/server/oauth/token";
    private static final String CHECK_TOKEN = DOMAIN + "/server/oauth/check_token";
    private static AccessToken ACCESS_TOKEN;

    // ###################################################### 下单 ######################################################
    private static final String PRODUCT_NATIVE_PAY = DOMAIN + "/server/api/wx-pay/native-product/{productPID}/{userId}";
    private static final String CENTS_NATIVE_PAY = DOMAIN + "/server/api/wx-pay/native-cents/{productPID}/{cents}/{userId}";
    private static final String PRODUCT_JSAPI_PRE_PAY = DOMAIN + "/server/api/wx-pay/jsapi-product/{productPID}/{userId}/{openid}";
    private static final String CENTS_JSAPI_PRE_PAY = DOMAIN + "/server/api/wx-pay/jsapi-cents/{productPID}/{cents}/{userId}/{openid}";
    private static final String REAL_JSAPI_PAY = DOMAIN + "/server/api/wx-pay/jsapiPaymentSign/{prepayId}";
    // ###################################################### 查询 ######################################################
    private static final String QUERY_ORDER_LIST = DOMAIN + "/server/api/order-info/list";
    private static final String QUERY_ORDER_INFO = DOMAIN + "/server/api/order-info/query-order-info/{orderNo}/{userId}";
    private static final String QUERY_PRODUCT_LIST = DOMAIN + "/server/api/product/list";
    private static final String QUERY_ORDER_STATUS = DOMAIN + "/server/api/order-info/query-order-status/{orderNo}/{userId}";
    // ################################################## 退款、取消订单 ##################################################
    private static final String CANCEL_ORDER = DOMAIN + "/server/api/wx-pay/cancel/{orderNo}";
    private static final String REFUND_ORDER = DOMAIN + "/server/api/wx-pay/refunds/{orderNo}/{reason}";
    // #################################################### 更新订单 #####################################################
    private static final String UPDATE_ORDER_STATUS = DOMAIN + "/server/api/order-info/update-order-status/{orderNo}";
    private static final String DOWNLOAD_BILL = DOMAIN + "/server/api/wx-pay/downloadbill/{billDate}/{type}";
    private static final String CLIENT_ID = "aZ5t7amfvn3lRXiiwx";
    private static final String CLIENT_SECRET = "iQm7Y9m5ropYzyqGEt8wTukXclBmjffr";
    private static final String PUBLIC_KEY_BASE64 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCeEuLSnjKZJQ/RWH3SPwJ2mOrGCUVDBxOpv/PpQusvCNrU02OA6iXsjP7lQ246da7ICYbIkru9t0qTlBqJI7Gq0JqMpU3gl/7De+wq0cpYzFbEExXS6eS6Z52wALEF4mY0Zy9MkaFvVMh/6xn5I197V8cvEAk5hKMawfRIw0lyWwIDAQAB";
    private static final String BILL_DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";
    private static final RequestBody EMPTY_REQUEST_BODY = RequestBody.create(null, new byte[0]);


    public static R nativePay(String productPID, String userIdentifier) {
        Request request = new Request.Builder()
                .post(EMPTY_REQUEST_BODY)
                .url(genericURL(PRODUCT_NATIVE_PAY, productPID, userIdentifier))
                .build();
        return genericHandleResponse(request);
    }

    public static R nativePay(String productPID, int cents, String identifier) {
        Request request = new Request.Builder()
                .post(EMPTY_REQUEST_BODY)
                .url(genericURL(CENTS_NATIVE_PAY, productPID, String.valueOf(cents), identifier))
                .build();
        return genericHandleResponse(request);
    }

    public static R jsapiPrePay(String productPID, String userIdentifier, String openId) {
        Request request = new Request.Builder()
                .post(EMPTY_REQUEST_BODY)
                .url(genericURL(PRODUCT_JSAPI_PRE_PAY, productPID, userIdentifier, openId))
                .build();
        return genericHandleResponse(request);
    }

    public static R jsapiPrePay(String productPID, int cents, String userIdentifier, String openId) {
        Request request = new Request.Builder()
                .post(EMPTY_REQUEST_BODY)
                .url(genericURL(CENTS_JSAPI_PRE_PAY, productPID, String.valueOf(cents), userIdentifier, openId))
                .build();
        return genericHandleResponse(request);
    }

    public static R realJsapiPay(String prepayId) {
        Request request = new Request.Builder()
                .url(genericURL(REAL_JSAPI_PAY, prepayId))
                .get()
                .build();
        return genericHandleResponse(request);
    }

    /**
     * 取消订单
     *
     * @param orderNo 订单编号
     * @return 订单状态
     */
    public static R cancelOrder(String orderNo) {
        Request request = new Request.Builder()
                .post(EMPTY_REQUEST_BODY)
                .url(genericURL(CANCEL_ORDER, orderNo))
                .build();
        return genericHandleResponse(request);
    }

    /**
     * 退款订单
     *
     * @param orderNo 订单编号
     * @param reason  退款原因
     * @return 订单状态
     */
    public static R refundOrder(String orderNo, String reason) {
        Request request = new Request.Builder()
                .post(EMPTY_REQUEST_BODY)
                .url(genericURL(REFUND_ORDER, orderNo, reason))
                .build();
        return genericHandleResponse(request);
    }


    /**
     * 下载账单
     *
     * @param billDate 账单日期
     * @param type     账单类型，tradebill 和 fundflowbill
     * @return 账单数据
     */
    public static R downloadBill(String billDate, String type) {

        if (!isValidDate(billDate)) {
            throw new InvalidBillDateException("提供的交易下载账单日期不正确", billDate);
        }

        Request request = new Request.Builder()
                .get()
                .url(genericURL(DOWNLOAD_BILL, billDate, type))
                .build();
        return genericHandleResponse(request);
    }

    /**
     * 查询所有订单
     *
     * @return 订单列表
     */
    public static R queryOrderList() {
        Request request = new Request.Builder()
                .get()
                .url(QUERY_ORDER_LIST)
                .build();
        return genericHandleResponse(request);
    }

    /**
     * 更新订单状态
     *
     * @param orderNo     订单编号
     * @param orderStatus 目标订单状态，通过 OrderStatus 设置，如 OrderStatus.SUCCESS
     * @return 订单信息
     */
    public static R updateOrderStatus(String orderNo, OrderStatus orderStatus) {

        Request request = new Request.Builder()
                .patch(RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), orderStatus.name()))
                .url(genericURL(UPDATE_ORDER_STATUS, orderNo))
                .build();
        return genericHandleResponse(request);
    }

    /**
     * 查询订单状态
     *
     * @param orderNo        订单编号
     * @param userIdentifier 用户唯一标识
     * @return 订单信息
     */
    public static R queryOrderStatus(String orderNo, String userIdentifier) {
        Request request = new Request.Builder()
                .get()
                .url(genericURL(QUERY_ORDER_STATUS, orderNo, userIdentifier))
                .build();
        return genericHandleResponse(request);
    }

    public static OrderInfo queryOrderInfo(String orderNo, String userIdentifier) throws OrderException {
        Request request = new Request.Builder()
                .get()
                .url(genericURL(QUERY_ORDER_INFO, orderNo, userIdentifier))
                .build();
        return handleResponse(request);
    }

    /**
     * 查询所有商品
     *
     * @return 商品列表
     */
    public static R queryProductList() {
        Request request = new Request.Builder()
                .get()
                .url(String.format(QUERY_PRODUCT_LIST))
                .build();
        return genericHandleResponse(request);
    }


    public static R checkOrder(String orderStatus) throws OrderTypeException {
        if (!StringUtils.hasText(orderStatus)) {
            return R.ok().setCode(50003).setMessage("订单不存在");
        }
        if (OrderStatus.NOTPAY.getType().equals(orderStatus)) {
            return R.ok().setCode(50002).setMessage("支付中");
        }
        if (OrderStatus.SUCCESS.getType().equals(orderStatus)) {
            return R.ok().setCode(50000).setMessage("支付成功");
        }
        if (OrderStatus.FINISHED.getType().equals(orderStatus)) {
            return R.ok().setCode(50001).setMessage("订单已完成");
        }
        if (OrderStatus.CANCEL.getType().equals(orderStatus)) {
            return R.ok().setCode(50004).setMessage("订单已取消");
        }
        if (OrderStatus.REFUND_SUCCESS.getType().equals(orderStatus)) {
            return R.ok().setCode(50005).setMessage("订单已退款");
        }
        if (OrderStatus.REFUND_PROCESSING.getType().equals(orderStatus)) {
            return R.ok().setCode(50006).setMessage("订单退款中");
        }
        if (OrderStatus.REFUND_ABNORMAL.getType().equals(orderStatus)) {
            return R.ok().setCode(50007).setMessage("订单退款异常");
        }
        if (OrderStatus.CLOSED.getType().equals(orderStatus)) {
            return R.ok().setCode(50008).setMessage("订单已超时");
        }
        throw new OrderTypeException("订单状态异常", orderStatus);
    }

    // ####################### private ####################### //
    private static R genericHandleResponse(Request request) {
        updateToken();
        try (Response response = AUTHORIZATION_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {

                return new Gson().fromJson(response.body().string(), R.class);
            }
            System.out.println(response.code());
            throw new RuntimeException("请求失败");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static OrderInfo handleResponse(Request request) {
        updateToken();
        try (Response response = AUTHORIZATION_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String resp = response.body().string();
                return new Gson().fromJson(resp, OrderInfo.class);
            }
            throw new RuntimeException("请求失败");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized static void updateToken() {
        // 5 分钟平滑期
        if (AUTHORIZATION_CLIENT != null && ACCESS_TOKEN != null && ACCESS_TOKEN.getExpireTime() > (System.currentTimeMillis() + (5 * 60 * 1000))) {
            log.info("cache token exists");
            return;
        }
        log.info("no cache");

        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY_BASE64)));

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = cipher.doFinal(CLIENT_SECRET.getBytes(StandardCharsets.UTF_8));
            String encryptClientSecret = Base64.getEncoder().encodeToString(bytes);

            String credentials = String.format("%s:%s", CLIENT_ID, encryptClientSecret);

            String credentialsB64 = "Basic ".concat(Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8)));

            RequestBody formBody = new FormBody.Builder(StandardCharsets.UTF_8)
                    .add("grant_type", AUTHORIZED_GRANT_TYPES)
                    .build();

            Request request = new Request.Builder().post(formBody).url(OAUTH_TOKEN)
                    .addHeader("Authorization", credentialsB64)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded").build();

            try (Response response = okHttpClient.newCall(request).execute()) {

                if (!response.isSuccessful() || response.body() == null) {
                    throw new RuntimeException("请求失败：" + response.code());
                }

                AccessToken accessToken = new Gson().fromJson(response.body().string(), AccessToken.class);
                accessToken.setExpireTime((System.currentTimeMillis() + (accessToken.expireTime * 1000L)));
                ACCESS_TOKEN = accessToken;

                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.addInterceptor(chain -> {
                    Request originalRequest = chain.request();

                    Request requestWithHeaders = originalRequest.newBuilder()
                            .header("Authorization", accessToken.tokenType.concat(" ").concat(accessToken.accessToken))
                            .build();

                    return chain.proceed(requestWithHeaders);
                });

                AUTHORIZATION_CLIENT = builder.build();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

    }

    private static boolean isValidDate(String dateStr) {
        Pattern pattern = Pattern.compile(BILL_DATE_PATTERN);
        Matcher matcher = pattern.matcher(dateStr);
        return matcher.matches();
    }

    @Data
    @AllArgsConstructor
//    @Accessors
    static class AccessToken {
        @SerializedName("access_token")
        private String accessToken;
        @SerializedName("token_type")
        private String tokenType;
        @SerializedName("expires_in")
        private long expireTime;
    }

    public static String genericURL(String format, String... args) {

        int endIndex;
        int beginIndex = format.indexOf('{');
        ArrayList<String> placeholders = new ArrayList<>();

        while (beginIndex != -1) {
            endIndex = format.indexOf('}', beginIndex + 1);
            if (endIndex == -1) {
                throw new IllegalArgumentException("传入的字符串格式不正确，须至少满足一对 {}");
            }
            placeholders.add(format.substring(beginIndex, endIndex + 1));
            beginIndex = format.indexOf('{', endIndex + 1);
        }
        if (placeholders.size() != args.length) {
            throw new IllegalArgumentException("占位符个数和提供的参数个数不符");
        }
        for (int i = 0; i < placeholders.size(); i++) {
            format = format.replace(placeholders.get(i), args[i]);
        }

        return format;
    }
}
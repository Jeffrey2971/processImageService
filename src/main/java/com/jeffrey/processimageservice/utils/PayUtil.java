package com.jeffrey.processimageservice.utils;

import com.google.gson.Gson;
import com.jeffrey.processimageservice.enums.OrderStatus;
import com.jeffrey.processimageservice.exception.exception.InvalidBillDateException;
import com.jeffrey.processimageservice.exception.exception.OrderTypeException;
import com.jeffrey.processimageservice.vo.R;
import okhttp3.*;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class PayUtil {
    private static final OkHttpClient AUTHORIZATION_CLIENT;
    private static final String DOMAIN = "https://www.processimage.cn";
//    private static final String DOMAIN = "http://localhost:8085";
    private static final String NATIVE_PAY = DOMAIN + "/server/api/wx-pay/native/%s/%s";
    private static final String JSAPI_PRE_PAY = DOMAIN + "/server/api/wx-pay/jsapi/%s/%s/%s";
    private static final String JSAPI_PAY = DOMAIN + "/server/api/wx-pay/jsapiPaymentSign/%s";
    private static final String CANCEL_ORDER = DOMAIN + "/server/api/wx-pay/cancel/%s";
    private static final String REFUND_ORDER = DOMAIN + "/server/api/wx-pay/refunds/%s/%s";
    private static final String QUERY_ORDER = DOMAIN + "/server/api/order-info/query-order-status/%s/%s";
    private static final String QUERY_PRODUCT_LIST = DOMAIN + "/server/api/product/list";
    private static final String QUERY_ORDER_LIST = DOMAIN + "/server/api/order-info/list";
    private static final String UPDATE_ORDER_STATUS = DOMAIN + "/server/api/order-info/update-order-status/%s";
    private static final String DOWNLOAD_BILL = DOMAIN + "/server/api/wx-pay/downloadbill/%s/%s";
    private static final String BasicAuthorizationUsername = "admin-jeffrey";
    private static final String BasicAuthorizationPassword = "Aa664490254";
    private static final String BILL_DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";


    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(chain -> {
            // 在这里可以添加全局的头部信息，如 Authorization
            // 以下示例添加了 Authorization 头部
            Request originalRequest = chain.request();
            String credentials = String.format("%s:%s", BasicAuthorizationUsername, BasicAuthorizationPassword);

            String credentialsB64 = Base64Utils.encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            Request requestWithHeaders = originalRequest.newBuilder()
                    .header("Authorization", "Basic " + credentialsB64)
                    .build();
            return chain.proceed(requestWithHeaders);
        });

        AUTHORIZATION_CLIENT = builder.build();
    }

    /**
     * Native 下单
     *
     *
     * @param productId      商品编号
     * @param userIdentifier 用户唯一标识
     * @return 订单信息
     */
    public static R nativePay(String productId, String userIdentifier) {
        Request request = new Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), new byte[0]))
                .url(String.format(NATIVE_PAY, productId, userIdentifier))
                .build();
        return handleResponse(request);
    }

    /**
     * JSAPI 预下单
     *
     * @param productId      商品编号
     * @param userIdentifier 用户唯一标识
     * @param openId         用户 openid
     * @return 订单预支付信息
     */
    public static R jsapiPrePay(String productId, String userIdentifier, String openId) {
        Request request = new Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), new byte[0]))
                .url(String.format(JSAPI_PRE_PAY, productId, userIdentifier, openId))
                .build();
        return handleResponse(request);
    }

    public static R jsapiPay(String prepayId){
        Request request = new Request.Builder()
                .url(String.format(JSAPI_PAY, prepayId))
                .get()
                .build();
        return handleResponse(request);
    }

    /**
     * 取消订单
     * @param orderNo 订单编号
     * @return 订单状态
     */
    public static R cancelOrder(String orderNo) {
        Request request = new Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), new byte[0]))
                .url(String.format(CANCEL_ORDER, orderNo))
                .build();
        return handleResponse(request);
    }

    /**
     * 退款订单
     * @param orderNo 订单编号
     * @param reason 退款原因
     * @return 订单状态
     */
    public static R refundOrder(String orderNo, String reason) {
        Request request = new Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/json;charset=utf-8"), new byte[0]))
                .url(String.format(REFUND_ORDER, orderNo, reason))
                .build();
        return handleResponse(request);
    }


    /**
     * 下载账单
     * @param billDate 账单日期
     * @param type 账单类型，tradebill 和 fundflowbill
     * @return 账单数据
     */
    public static R downloadBill(String billDate, String type){

        if (!isValidDate(billDate)) {
            throw new InvalidBillDateException("提供的交易下载账单日期不正确", billDate);
        }

        Request request = new Request.Builder()
                .get()
                .url(String.format(DOWNLOAD_BILL, billDate, type))
                .build();
        return handleResponse(request);
    }

    /**
     * 查询所有订单
     * @return 订单列表
     */
    public static R queryOrderList(){
        Request request = new Request.Builder()
                .get()
                .url(QUERY_ORDER_LIST)
                .build();
        return handleResponse(request);
    }

    /**
     * 更新订单状态
     * @param orderNo 订单编号
     * @param orderStatus 目标订单状态，通过 OrderStatus 设置，如 OrderStatus.SUCCESS
     * @return 订单信息
     */
    public static R updateOrderStatus(String orderNo, OrderStatus orderStatus) {

        Request request = new Request.Builder()
                .patch(RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), orderStatus.name()))
                .url(String.format(UPDATE_ORDER_STATUS, orderNo))
                .build();
        return handleResponse(request);
    }

    /**
     * 查询订单状态
     * @param orderNo 订单编号
     * @param userIdentifier 用户唯一标识
     * @return 订单信息
     */
    public static R queryOrderStatus(String orderNo, String userIdentifier) {
        Request request = new Request.Builder()
                .get()
                .url(String.format(QUERY_ORDER, orderNo, userIdentifier))
                .build();
        return handleResponse(request);
    }

    /**
     * 查询所有商品
     * @return 商品列表
     */
    public static R queryProductList(){
        Request request = new Request.Builder()
                .get()
                .url(String.format(QUERY_PRODUCT_LIST))
                .build();
        return handleResponse(request);
    }

    public static R checkOrder(String orderStatus) throws OrderTypeException{
        if (!StringUtils.hasText(orderStatus)) {
            return R.ok().setMessage("订单不存在");
        }
        if (OrderStatus.NOTPAY.getType().equals(orderStatus)) {
            return R.ok().setCode(101).setMessage("支付中");
        }
        if (OrderStatus.SUCCESS.getType().equals(orderStatus)) {
            return R.ok().setMessage("支付成功");
        }
        if (OrderStatus.FINISHED.getType().equals(orderStatus)) {
            return R.ok().setMessage("订单已完成");
        }
        if (OrderStatus.CANCEL.getType().equals(orderStatus)) {
            return R.ok().setCode(99).setMessage("订单已取消");
        }
        if (OrderStatus.REFUND_SUCCESS.getType().equals(orderStatus)) {
            return R.ok().setMessage("订单已退款");
        }
        if (OrderStatus.REFUND_PROCESSING.getType().equals(orderStatus)) {
            return R.ok().setMessage("订单退款中");
        }
        if (OrderStatus.REFUND_ABNORMAL.getType().equals(orderStatus)) {
            return R.ok().setMessage("订单退款异常");
        }
        if (OrderStatus.CLOSED.getType().equals(orderStatus)) {
            return R.ok().setCode(100).setMessage("订单已超时");
        }
        throw new OrderTypeException("订单状态异常", orderStatus);
    }

    // ####################### private ####################### //
    private static R handleResponse(Request request) {
        try (Response response = AUTHORIZATION_CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {

                return new Gson().fromJson(response.body().string(), R.class);
            }
            throw new RuntimeException("请求失败");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isValidDate(String dateStr) {
        Pattern pattern = Pattern.compile(BILL_DATE_PATTERN);
        Matcher matcher = pattern.matcher(dateStr);
        return matcher.matches();
    }
    public static void main(String[] args) {
        System.out.println(PayUtil.updateOrderStatus("ORDER_20231110093936848", OrderStatus.REFUND_PROCESSING));
    }
}

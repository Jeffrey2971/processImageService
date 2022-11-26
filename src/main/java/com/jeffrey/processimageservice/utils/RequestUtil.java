package com.jeffrey.processimageservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Slf4j
@Component
public class RequestUtil {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    static {
        REST_TEMPLATE.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }


    /**
     * 携带请求参数和响应类型的 GET 请求，返回本次请求的状态信息以及响应体
     * @param url 目标地址
     * @param clazz 将请求的结果封装到给定的 clazz 对象中
     * @param httpParams 本次请求所携带的参数
     * @param <T> 返回类型
     * @return ResponseEntity<T>
     */
    public static <T> ResponseEntity<T> getEntity(String url, Class<T> clazz, Map<String, Object> httpParams) {
        log.info("请求地址：{}", url);
        return REST_TEMPLATE.getForEntity(url, clazz, httpParams);
    }

    public static <T> T getObject(String url, Class<T> responseType, Map<String, Object> httpParams) {
        log.info("请求地址：{}", url);
        return REST_TEMPLATE.getForObject(url, responseType, httpParams);
    }

    public static <T> ResponseEntity<T> getEntity(String url, Class<T> clazz) {
        log.info("请求地址：{}", url);
        return REST_TEMPLATE.getForEntity(url, clazz);
    }

    public static <T> T getObject(String url, Class<T> responseType) {
        log.info("请求地址：{}", url);
        return REST_TEMPLATE.getForObject(url, responseType);
    }

    public static <T> ResponseEntity<T> postEntity(String url, Class<T> clazz, Map<String, Object> requestBody){
        log.info("请求地址：{}", url);
        return null;
    }
    /**
     *
     * @param url 目标地址
     * @param httpParams 本次请求携带的参数，只能使用 LinkedMultiValueMap
     * @param responseType 本次请求的响应类型
     * @param <T> 返回类型
     * @return ResponseEntity<T>
     */
    public static <T> ResponseEntity<T> postEntity(String url, LinkedMultiValueMap<String, Object> httpParams, HttpHeaders httpHeaders, Class<T> responseType) {
        log.info("请求地址：{}", url);
        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(httpParams, httpHeaders);
        return REST_TEMPLATE.postForEntity(url, httpEntity, responseType);
    }

    /**
     * 携带请求参数、请求体发送一个 POST 请求并返回本次响应的数据
     * @param url 目标地址
     * @param httpEntity HttpEntity 中包括了本次请求的请求头、请求体等信息
     * @param responseType 本次请求响应的类型，该类型决定了 ResponseEntity 的泛型
     * @param httpParams 本次请求所携带的参数
     * @param <T> 返回类型
     * @return ResponseEntity<T>
     */
    public static <T> ResponseEntity<T> postEntity(String url, HttpEntity httpEntity, Class<T> responseType, @Nullable Map<String, Object> httpParams) {
        if (httpParams == null || httpParams.isEmpty()) {
            return REST_TEMPLATE.postForEntity(url, httpEntity, responseType);
        }
        return REST_TEMPLATE.postForEntity(url, httpEntity, responseType, httpParams);

    }

    public static <T> T postObject(String url, String requestBody, Class<T> responseType, @Nullable Map<String, Object> httpParams) {
        if (httpParams != null) {

            return REST_TEMPLATE.postForObject(url, requestBody, responseType, httpParams);
        }
        return REST_TEMPLATE.postForObject(url, requestBody, responseType);
    }

}

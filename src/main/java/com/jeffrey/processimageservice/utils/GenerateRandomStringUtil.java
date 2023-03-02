package com.jeffrey.processimageservice.utils;

import org.springframework.util.StringUtils;

import java.security.SecureRandom;

/**
 * 一个生成随机字符串的工具类，可根据传入的字符串（大小写英文字母、数字、特殊符号）随机生成指定长度的随机字符串
 *
 * @author jeffrey
 * @since JDK 1.8
 */


public class GenerateRandomStringUtil {
    public static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    public static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    public static final String NUMBER = "0123456789";
    public static final String SPECIAL_CHARS = "!@#$%^&*_=+-/";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 根据传入的字符串（大小写英文字母、数字、特殊符号）随机生成指定长度的随机字符串
     * GenerateRandomStringUtil 类中提供了这些静态常量字符，分别为 CHAR_LOWER / CHAR_UPPER / NUMBER / SPECIAL_CHARS
     * 例如 GenerateRandomStringUtil.createRandomStr(NUMBER + SPECIAL_CHARS, 8);
     * @param participateChar
     * @param length
     * @return
     */
    public synchronized static String createRandomStr(String participateChar, int length) {

        if (!StringUtils.hasText(participateChar) || length <= 0) {
            throw new IllegalArgumentException();
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int rndCharAt = RANDOM.nextInt(participateChar.length());
            char rndChar = participateChar.charAt(rndCharAt);
            sb.append(rndChar);
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(createRandomStr(NUMBER + CHAR_UPPER, 8));
    }
}

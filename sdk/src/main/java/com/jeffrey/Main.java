package com.jeffrey;

import okhttp3.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {

        // 计算图像的 MD5 值
        String imageMd5 = calculateMd5(Const.IMAGE_FILE.toPath(), null);

        // 生成一个随机数，范围是 [10^7, 10^8-1] ，将数字转换成字符串，并在前面补 0 ，使长度为8位
        String salt = String.format("%08d", new Random().nextInt(100000000 - 10000000) + 10000000);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();


        // 创建多部分请求体
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uploadFile", "image.jpg", RequestBody.create(MediaType.parse("image/jpeg"), Const.IMAGE_FILE))
                .addFormDataPart("publicKey", Const.PUBLIC_KEY)
                .addFormDataPart("salt", salt);

                /*
                    --------------- 以下参数不是必须的，请根须需要参照 api 文档填写，某些参数提供了对应的工具类，例如 Offset ---------------

                    requestBodyBuilder.addFormDataPart("offset", "see api document");
                    requestBodyBuilder.addFormDataPart("sync", "see api document");
                    requestBodyBuilder.addFormDataPart("callback", "see api document");
                    requestBodyBuilder.addFormDataPart("rectangles", "see api document");
                    requestBodyBuilder.addFormDataPart("markNames", "see api document");
                    requestBodyBuilder.addFormDataPart("excludeKeywords", "");
                    requestBodyBuilder.addFormDataPart("onlyOcr", "true or false");
                    requestBodyBuilder.addFormDataPart("show", "true or false");
                 */

        RequestBody requestBody = requestBodyBuilder.build();

        // 计算签名
        String apiSign = calculateMd5(null, Const.PUBLIC_KEY + imageMd5 + salt + Const.PRIVATE_KEY);

        // 创建请求
        Request request = new Request.Builder()
                .url(Const.TARGET_URL)
                .post(requestBody)
                .addHeader("x-access-api-sign", apiSign)
                .build();

        // 发送请求并获取响应
        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                System.out.println(response.body().string());
            }
        }
    }

    /**
     * 通用方法，用于计算图片的 md5 以及字符串的 md5，参数 filePath 以及 str 同时只能传递一个，如同时提供则以 filePath 优先级更高
     * @param filePath 图片路径
     * @param str 字符串
     * @return 图片或字符串的 md5
     * @throws Exception Exception
     */
    private static String calculateMd5(Path filePath, String str) throws Exception {

        // 创建实例
        MessageDigest md = MessageDigest.getInstance("MD5");

        if (filePath != null) {

            // 计算文件的 md5r

            try (InputStream is = Files.newInputStream(filePath)) {
                int numBytes;
                byte[] buffer = new byte[1024];
                while ((numBytes = is.read(buffer)) != -1) {
                    md.update(buffer, 0, numBytes);
                }
            }

        } else if (str != null) {

            // 计算字符串的 md5

            md.update(str.getBytes(StandardCharsets.UTF_8));

        } else {

            throw new RuntimeException("需至少提供一个参数，filePath 或 str");

        }

        byte[] digest = md.digest();

        try (Formatter formatter = new Formatter()) {
            for (byte b : digest) {
                formatter.format("%02x", b);
            }
            return formatter.toString().toLowerCase();
        }
    }
}
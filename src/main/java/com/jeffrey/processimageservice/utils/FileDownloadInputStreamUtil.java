package com.jeffrey.processimageservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Slf4j
public class FileDownloadInputStreamUtil {

    public static synchronized InputStream download(String httpUrl) throws IOException {

        if (!StringUtils.hasText(httpUrl)) {
            log.error("提供的链接不能为空");
        }

        log.info("开始下载文件字节流：{}", httpUrl);

        URLConnection urlConnection = new URL(httpUrl).openConnection();

        urlConnection.setUseCaches(true);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setReadTimeout(15000);

        return urlConnection.getInputStream();
    }
}

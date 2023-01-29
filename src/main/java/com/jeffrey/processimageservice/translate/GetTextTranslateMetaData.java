package com.jeffrey.processimageservice.translate;

import com.jeffrey.processimageservice.conf.TranslateProperties;
import com.jeffrey.processimageservice.entities.translate.TranslationData;
import com.jeffrey.processimageservice.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * 根据给定的图片返回该图片中的数据信息
 */
@Component
@Slf4j
public class GetTextTranslateMetaData {

    private static TranslateProperties translateProperties;

    @Autowired
    public void setTranslateProperties(TranslateProperties translateProperties) {
        GetTextTranslateMetaData.translateProperties = translateProperties;
    }

    /**
     * @param imageInputStream 下载的图片字节数据
     * @return 响应体封装对象
     * 传递图片字节数据，返回 TranslationData 对象
     * TranslationData 内部封装了关于图片中的信息
     */
    public static TranslationData getData(InputStream imageInputStream) throws IOException {

        log.info("获取图片数据");

        ByteArrayResource bar = new ByteArrayResource(FileCopyUtils.copyToByteArray(imageInputStream)) {
            @Override
            public String getFilename() {
                return "image.png";
            }
        };

        String imageMd5 = DigestUtils.md5DigestAsHex(FileCopyUtils.copyToByteArray(bar.getInputStream()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        httpHeaders.setContentLength(bar.contentLength());

        StringBuilder salt = new StringBuilder(9);

        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            salt.append(random.nextInt(9));
        }

        LinkedMultiValueMap<String, Object> transValueMap = new LinkedMultiValueMap<>();

        transValueMap.add("image", bar);
        transValueMap.add("from", translateProperties.getFrom());
        transValueMap.add("to", translateProperties.getTo());
        transValueMap.add("appid", translateProperties.getAppId());
        transValueMap.add("salt", salt.toString());
        transValueMap.add("cuid", translateProperties.getCuid());
        transValueMap.add("mac", translateProperties.getMac());
        transValueMap.add("version", translateProperties.getVersion());
        transValueMap.add("paste", translateProperties.getPaste());
        transValueMap.add("sign",
                DigestUtils.md5DigestAsHex((translateProperties.getAppId()
                        + imageMd5 + salt
                        + translateProperties.getCuid()
                        + translateProperties.getMac()
                        + translateProperties.getAppKey()).getBytes()
                ).toLowerCase());

        HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(transValueMap, httpHeaders);
        ResponseEntity<TranslationData> response = RequestUtil.postEntity(translateProperties.getApi(), httpEntity, TranslationData.class, null);

        transValueMap.clear();

        if (response.getStatusCodeValue() == 200 && response.getBody() != null) {
            return response.getBody();
        }

        throw new IOException();

    }
}

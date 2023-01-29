package com.jeffrey.processimageservice.service.impl;

import com.jeffrey.processimageservice.conf.TranslateProperties;
import com.jeffrey.processimageservice.entities.translate.TranslationData;
import com.jeffrey.processimageservice.exception.exception.ProcessImageFailedException;
import com.jeffrey.processimageservice.service.TranslateService;
import com.jeffrey.processimageservice.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j
public class TranslateServiceImpl implements TranslateService {

    private static TranslateProperties translateProperties;

    @Autowired
    public void setTranslateProperties(TranslateProperties translateProperties) {
        TranslateServiceImpl.translateProperties = translateProperties;
    }

    @Override
    public TranslationData getData(InputStream imageInputStream) throws ProcessImageFailedException{

        ByteArrayResource bar;
        String imageMd5;

        try {
            bar = new ByteArrayResource(FileCopyUtils.copyToByteArray(imageInputStream)) {

                /**
                 * 必须重写 getFilename 方法并指定名称，否则请求失败
                 * @return image name
                 */
                @Override
                public String getFilename() {
                    return "image.png";
                }
            };

            imageMd5 = DigestUtils.md5DigestAsHex(FileCopyUtils.copyToByteArray(bar.getInputStream()));
        } catch (IOException e) {
            throw new ProcessImageFailedException("处理图片过程中出现异常");
        }

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

        log.warn(response.getBody().toString());

        throw new ProcessImageFailedException("处理图片过程中出现异常");

    }
}

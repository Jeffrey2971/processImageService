package com.jeffrey.processimageservice.service.impl;

import com.jeffrey.processimageservice.entities.Info;
import com.jeffrey.processimageservice.entities.UpdatePublicAccountParams;
import com.jeffrey.processimageservice.entities.publicAccount.InitializationUserInfo;
import com.jeffrey.processimageservice.mapper.PublicAccountServiceImplMapper;
import com.jeffrey.processimageservice.service.PublicAccountService;
import com.jeffrey.processimageservice.utils.RequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
@Slf4j
public class PublicAccountServiceImpl implements PublicAccountService {

    private final Info info;
    private final PublicAccountServiceImplMapper publicAccountServiceImplMapper;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd hh-mm-ss");
    ;

    @Autowired
    public PublicAccountServiceImpl(Info info, PublicAccountServiceImplMapper publicAccountServiceImplMapper) {
        this.info = info;
        this.publicAccountServiceImplMapper = publicAccountServiceImplMapper;
    }

    @Override
    public boolean isPublicAccountUser(String openid) {
        String accessToken = getPublicAccountToken();
        String getUserInfoUrl = info.getGetUserInfoUrl().replace("OPENID", openid).replace("ACCESS_TOKEN", accessToken);
        String accountUserInfoJsonStr = RequestUtil.getObject(getUserInfoUrl, String.class);
        return !(accountUserInfoJsonStr.contains("invalid openid") && accountUserInfoJsonStr.contains("40003"));
    }

    @Override
    public String getPublicAccountToken() {
        try (
                BufferedReader br = new BufferedReader(new FileReader(info.getSecretKeyLocation()))
        ) {
            String key = br.readLine();
            String url = info.getGetPublicAccountTokenUrl();

            return RequestUtil.getObject(url + key, String.class);

        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }

    }

    @Override
    public boolean isFirstAccess(String openid) {
        Integer result = publicAccountServiceImplMapper.isFirstAccess(openid);
        return result == null || result == 0;
    }

    @Override
    public void initializationPublicUser(String openid) {
        publicAccountServiceImplMapper.initPublicAccountUser(
                new InitializationUserInfo(
                        null, openid, 10, 0, SDF.format(new Date(System.currentTimeMillis()))
                        )
        );
        log.info("初始化了一条公众号用户数据：{}", openid);
    }
    @Override
    public boolean updatePublicUserAccount(UpdatePublicAccountParams updatePublicAccountParams) {
        Integer result = publicAccountServiceImplMapper.updatePublicUserAccount(updatePublicAccountParams);
        return result != null && result > 0;
    }

    @Override
    public UpdatePublicAccountParams selectUserInfoByOpenid(String openid) {
        return publicAccountServiceImplMapper.selectUserInfoByOpenid(openid);
    }
}

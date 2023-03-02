package com.jeffrey.processimageservice.service;

import com.jeffrey.processimageservice.entities.UpdatePublicAccountParams;
import org.springframework.stereotype.Service;

@Service
public interface PublicAccountService {

    boolean isPublicAccountUser(String openid);

    String getPublicAccountToken();

    boolean isFirstAccess(String openid);

    void initializationPublicUser(String openid);

    boolean updatePublicUserAccount(UpdatePublicAccountParams updatePublicAccountParams);

    UpdatePublicAccountParams selectUserInfoByOpenid(String openid);
}

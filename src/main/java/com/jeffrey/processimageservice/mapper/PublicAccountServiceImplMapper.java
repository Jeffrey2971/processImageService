package com.jeffrey.processimageservice.mapper;

import com.jeffrey.processimageservice.entities.UpdatePublicAccountParams;
import com.jeffrey.processimageservice.entities.publicAccount.InitializationUserInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicAccountServiceImplMapper {
    Integer isFirstAccess(String openid);

    void initPublicAccountUser(InitializationUserInfo initializationUserInfo);

    Integer updatePublicUserAccount(UpdatePublicAccountParams updatePublicAccountParams);

    UpdatePublicAccountParams selectUserInfoByOpenid(String openid);
}

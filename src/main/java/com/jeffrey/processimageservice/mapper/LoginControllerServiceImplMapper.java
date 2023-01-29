package com.jeffrey.processimageservice.mapper;

import com.jeffrey.processimageservice.entities.sign.AccountInfo;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginControllerServiceImplMapper {
    AccountInfo selectAccountInfoByUsername(String username);

    EncryptedInfo queryAuthorizationInformationDataByUserName(String username);
}

package com.jeffrey.processimageservice.service.impl;

import com.jeffrey.processimageservice.entities.sign.AccountInfo;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.mapper.LoginControllerServiceImplMapper;
import com.jeffrey.processimageservice.service.LoginControllerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class LoginControllerServiceImpl implements LoginControllerService {

    private final LoginControllerServiceImplMapper loginControllerServiceImplMapper;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public LoginControllerServiceImpl(LoginControllerServiceImplMapper loginControllerServiceImplMapper, BCryptPasswordEncoder encoder) {
        this.loginControllerServiceImplMapper = loginControllerServiceImplMapper;
        this.encoder = encoder;
    }

    @Override
    public boolean normalUser(String username) {
        return false;
    }

    @Override
    public boolean loginSuccess(String pwd1, String pwd2) {


        return encoder.matches(pwd1, pwd2);
    }

    @Override
    public EncryptedInfo prepareData(String username) {
        EncryptedInfo encryptedInfo = loginControllerServiceImplMapper.queryAuthorizationInformationDataByUserName(username);
        return encryptedInfo;
    }

    @Override
    public AccountInfo getAccountInfoByUsername(String username) {
        return loginControllerServiceImplMapper.selectAccountInfoByUsername(username);
    }
}

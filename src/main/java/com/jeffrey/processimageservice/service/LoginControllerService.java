package com.jeffrey.processimageservice.service;

import com.jeffrey.processimageservice.entities.sign.AccountInfo;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import org.springframework.web.servlet.ModelAndView;

public interface LoginControllerService {
    boolean normalUser(String username);

    boolean loginSuccess(String pwd1, String pwd2);

    EncryptedInfo prepareData(String username);

    AccountInfo getAccountInfoByUsername(String username);
}

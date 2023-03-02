package com.jeffrey.processimageservice.service;

import com.github.pagehelper.PageInfo;
import com.jeffrey.processimageservice.entities.CreateMail;
import com.jeffrey.processimageservice.entities.ResetParams;
import com.jeffrey.processimageservice.entities.enums.ProcessStatusEnum;
import com.jeffrey.processimageservice.entities.sign.AccountInfo;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.vo.PageInnerData;

public interface LoginControllerService {

    boolean loginSuccess(String pwd1, String pwd2);
    EncryptedInfo prepareData(String username);
    AccountInfo getAccountInfoByUsername(String username);
    AccountInfo getAccountInfoByEmail(String email);
    PageInfo<PageInnerData> getPage(Integer page, Integer limit);
    Long getAllCount();
    void sessionLogout();

    ProcessStatusEnum resetPasswordAtLogin(ResetParams resetParams);
    ProcessStatusEnum resetPasswordAtForget(ResetParams resetParams);

    boolean sendResetMail(CreateMail createMail);

    CreateMail prepareEmailBody(String username, String mail);

    CreateMail prepareResetMailBody(String username, String mail);

    boolean checkVerificationCode(String mail, String code);

    ProcessStatusEnum checkToken(String token);
}

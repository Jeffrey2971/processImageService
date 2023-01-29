package com.jeffrey.processimageservice.service;

import com.jeffrey.processimageservice.entities.sign.AccountInfo;

public interface AccountService {

    AccountInfo getAccountInfoById(Integer id);

    void updateAccountInfo(AccountInfo accountInfo);
}

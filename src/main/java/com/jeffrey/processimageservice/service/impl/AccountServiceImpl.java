package com.jeffrey.processimageservice.service.impl;

import com.jeffrey.processimageservice.entities.sign.AccountInfo;
import com.jeffrey.processimageservice.mapper.AccountServiceImplMapper;
import com.jeffrey.processimageservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountServiceImplMapper accountServiceImplMapper;

    @Autowired
    public AccountServiceImpl(AccountServiceImplMapper accountServiceImplMapper) {
        this.accountServiceImplMapper = accountServiceImplMapper;
    }

    @Override
    public AccountInfo getAccountInfoById(Integer id) {
        return accountServiceImplMapper.getAccountInfoById(id);
    }

    @Override
    public void updateAccountInfo(AccountInfo accountInfo) {
        accountServiceImplMapper.updateAccountInfoById(accountInfo);
    }
}

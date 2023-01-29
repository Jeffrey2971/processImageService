package com.jeffrey.processimageservice.mapper;

import com.jeffrey.processimageservice.entities.sign.AccountInfo;
import org.springframework.stereotype.Repository;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Repository
public interface AccountServiceImplMapper {

    AccountInfo getAccountInfoById(Integer id);

    void updateAccountInfoById(AccountInfo accountInfo);
}

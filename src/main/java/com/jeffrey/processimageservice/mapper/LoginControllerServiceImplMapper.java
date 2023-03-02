package com.jeffrey.processimageservice.mapper;

import com.jeffrey.processimageservice.entities.sign.AccountInfo;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.vo.PageInnerData;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginControllerServiceImplMapper {
    AccountInfo selectAccountInfoByUsername(String username);
    AccountInfo selectAccountInfoByEmail(String email);
    EncryptedInfo queryAuthorizationInformationDataByUserName(String username);

    List<PageInnerData> getPage(Integer aid);

    Long getAllCount(Integer aid);

    Integer updatePasswordByUsername(String username, String newPassword);
    Integer updatePasswordByMail(String mail, String newPassword);
}

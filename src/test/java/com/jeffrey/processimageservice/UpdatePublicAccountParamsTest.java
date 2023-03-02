package com.jeffrey.processimageservice;

import com.jeffrey.processimageservice.entities.UpdatePublicAccountParams;
import com.jeffrey.processimageservice.service.PublicAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@SpringBootTest
@ActiveProfiles("dev")
public class UpdatePublicAccountParamsTest {


    private final PublicAccountService publicAccountService;

    @Autowired
    public UpdatePublicAccountParamsTest(PublicAccountService publicAccountService) {
        this.publicAccountService = publicAccountService;
    }

    @BeforeEach
    public void setUp(){
        System.setProperty("testing", "true");

    }

    @Test
    void update(){

        System.setProperty("testing", "true");

        UpdatePublicAccountParams updatePublicAccountParams = new UpdatePublicAccountParams();
        updatePublicAccountParams.setAllUse(99);
        updatePublicAccountParams.setCanUse(1);
        updatePublicAccountParams.setLastModifyTime("2023-02-13 05:28:22");
        updatePublicAccountParams.setOpenid("ovAP56mwhHP9_R1PsN3YTMNd2iog");
        boolean b = publicAccountService.updatePublicUserAccount(updatePublicAccountParams);
        System.out.println(b);
    }

}

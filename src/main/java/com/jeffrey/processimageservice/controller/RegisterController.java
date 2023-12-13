package com.jeffrey.processimageservice.controller;

import com.jeffrey.processimageservice.entities.register.RegisterParams;
import com.jeffrey.processimageservice.enums.LoginStatus;
import com.jeffrey.processimageservice.exception.exception.clitent.RegisterException;
import com.jeffrey.processimageservice.service.RegisterService;
import com.jeffrey.processimageservice.security.Decrypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Controller
@RequestMapping(value = "/user/register")
@Slf4j
public class RegisterController {

    private final RegisterService registerService;
    private final Decrypt decrypt;

    @Autowired
    public RegisterController(RegisterService registerService, Decrypt decrypt) {
        this.registerService = registerService;
        this.decrypt = decrypt;
    }

    @PostMapping
    @ResponseBody
    public LoginStatus register(RegisterParams registerParams) {

        if (!registerService.paramsCheck(registerParams)) {
            throw new RegisterException("注册失败，非法参数");
        }

        try {
            Integer aid = registerService.register(registerParams);
            registerService.creationKey(aid);
            return LoginStatus.SC_REGISTER_SUCCESS;
        } catch (RegisterException e) {
            e.printStackTrace();
            return LoginStatus.NO;
        }
    }


    @GetMapping("/async-check")
    @ResponseBody
    public Enum<LoginStatus> ajaxCheck(@RequestParam String action, @RequestParam String value) throws Exception {

        value = decrypt.rsaDecrypt(value);

        switch (action) {
            case "username":
                return registerService.usernameCanUse(value);
            case "email":
                return registerService.emailCanUse(value);
            default:
                return null;
        }
    }
}
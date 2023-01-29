package com.jeffrey.processimageservice.controller;

import com.jeffrey.processimageservice.entities.RegisterParams;
import com.jeffrey.processimageservice.entities.enums.LoginStatus;
import com.jeffrey.processimageservice.exception.exception.clitent.RegisterException;
import com.jeffrey.processimageservice.service.RegisterControllerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Controller()
@RequestMapping(value = "/register")
@Slf4j
public class RegisterController {

    private final RegisterControllerService registerControllerService;

    @Autowired
    public RegisterController(RegisterControllerService registerControllerService) {
        this.registerControllerService = registerControllerService;
    }

    @PostMapping
    @ResponseBody
    public LoginStatus register(RegisterParams registerParams) {

        if (!registerControllerService.paramsCheck(registerParams)) {
            throw new RegisterException("注册失败，非法参数");
        }

        try {
            Integer aid = registerControllerService.register(registerParams);
            registerControllerService.creationKey(aid);
            return LoginStatus.SC_REGISTER_SUCCESS;
        } catch (RegisterException e) {
            return LoginStatus.NO;
        }
    }


    @GetMapping("/async-check")
    @ResponseBody
    public Enum<LoginStatus> ajaxCheck(@RequestParam String action, @RequestParam String value) {

        switch (action) {
            case "username":
                return registerControllerService.usernameIsExists(value);
            case "email":
                return registerControllerService.emailIsExists(value);
            default:
                return null;
        }
    }
}

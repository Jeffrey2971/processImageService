package com.jeffrey.processimageservice.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.jeffrey.processimageservice.entities.ResponseObject;
import com.jeffrey.processimageservice.entities.sign.AccountInfo;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.service.LoginControllerService;
import com.jeffrey.processimageservice.service.RegisterControllerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Controller
@Slf4j
public class LoginController {
    private final LoginControllerService loginControllerService;
    private final RegisterControllerService registerControllerService;

    public LoginController(LoginControllerService loginControllerService, RegisterControllerService registerControllerService) {
        this.loginControllerService = loginControllerService;
        this.registerControllerService = registerControllerService;
    }

    @GetMapping("/sessionLogOut")
    @ResponseBody
    public ResponseObject sessionLogout(){
        try {
            StpUtil.logout();
        } catch (Exception e) {
            log.error("退出登录失败：", e);

            ResponseObject responseObject = new ResponseObject();
            responseObject.setCode(0);
            responseObject.setMsg("退出登陆失败");
            return responseObject;
        }

        ResponseObject responseObject = new ResponseObject();
        responseObject.setCode(200);
        responseObject.setMsg("退出登陆成功");
        return responseObject;
    }

    @GetMapping("/login")
    public String jumpLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseObject login(String username, String password) {

        if (!StringUtils.isBlank(username) && !StringUtils.isBlank(password)) {

            if (registerControllerService.usernameIsLegal(username) && registerControllerService.passwordIsLegal(password)) {

                AccountInfo accountInfo = loginControllerService.getAccountInfoByUsername(username);

                if (accountInfo != null && loginControllerService.loginSuccess(password, accountInfo.getPassword())) {

                    EncryptedInfo encryptedInfo = loginControllerService.prepareData(username);

                    StpUtil.login(accountInfo.getId(), true);

                    StpUtil.getSession().set(
                            "publicKey", encryptedInfo.getPublicKey()
                    ).set(
                            "privateKey", encryptedInfo.getPrivateSecret()
                    ).set(
                            "username", username
                    ).set(
                            "apiCanUseCount", accountInfo.getApiCanUseCount()
                    ).set(
                            "apiUsedCount", accountInfo.getApiUsedCount()
                    );

                    ResponseObject responseObject = new ResponseObject();

                    responseObject.setData(StpUtil.getTokenValue());
                    responseObject.setCode(200);
                    responseObject.setMsg("登陆成功");

                    return responseObject;

                }
            }
        }

        ResponseObject responseObject = new ResponseObject();
        responseObject.setCode(403);
        responseObject.setMsg("账号或密码错误");
        responseObject.setData(null);

        return responseObject;

    }

    @PostMapping("/change")
    @ResponseBody
    public String jumpPage(@RequestParam String currentPassword, @RequestParam String newPassword, @RequestParam String confirmPassword){
        System.out.println(currentPassword);
        System.out.println(newPassword);
        System.out.println(confirmPassword);
        return "no ready";
    }

    @GetMapping("/change")
    public String changePassword(){
        return "reset";
    }

    @GetMapping("/main")
    public ModelAndView main(ModelAndView mav) {

        try {
            StpUtil.checkLogin();
        } catch (RuntimeException e) {
            mav.setViewName("redirect:/login");
            return mav;
        }

        mav.addObject("publicKey", StpUtil.getSession().get("publicKey"));
        mav.addObject("privateKey", StpUtil.getSession().get("privateKey"));
        mav.addObject("username", StpUtil.getSession().get("username"));
        mav.addObject("apiCanUseCount", StpUtil.getSession().get("apiCanUseCount"));
        mav.addObject("apiUsedCount", StpUtil.getSession().get("apiUsedCount"));

        mav.setViewName("main");

        return mav;
    }
}

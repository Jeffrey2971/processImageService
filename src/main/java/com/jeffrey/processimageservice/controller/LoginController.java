package com.jeffrey.processimageservice.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageInfo;
import com.jeffrey.processimageservice.entities.CreateMail;
import com.jeffrey.processimageservice.entities.Info;
import com.jeffrey.processimageservice.entities.ResetParams;
import com.jeffrey.processimageservice.enums.ProcessStatusEnum;
import com.jeffrey.processimageservice.entities.register.GenericLoginParams;
import com.jeffrey.processimageservice.entities.response.ResponseObject;
import com.jeffrey.processimageservice.entities.sign.AccountInfo;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.exception.exception.clitent.AccountPasswordResetException;
import com.jeffrey.processimageservice.security.Decrypt;
import com.jeffrey.processimageservice.service.LoginService;
import com.jeffrey.processimageservice.service.RegisterService;
import com.jeffrey.processimageservice.vo.Page;
import com.jeffrey.processimageservice.vo.PageInnerData;
import com.jeffrey.processimageservice.vo.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Controller
@RequestMapping("/user")
@Slf4j
public class LoginController {

    private final LoginService loginService;
    private final RegisterService registerService;
    private final Info info;
    private final Decrypt decrypt;

    public LoginController(LoginService loginService, RegisterService registerService, Info info, Decrypt decrypt) {
        this.loginService = loginService;
        this.registerService = registerService;
        this.info = info;
        this.decrypt = decrypt;
    }

    /**
     * 跳转至 login 视图，为一个登陆和注册页
     *
     * @return login.html 视图
     */
    @GetMapping("/login")
    public String jumpLoginPage() {
        return "login";
    }

    @GetMapping("/status")
    @ResponseBody
    public R statusCheck(){
        boolean isLogin = StpUtil.isLogin();
        return new R().setCode(isLogin ? 0 : 401).setMessage(isLogin ? "yep" : "no");
    }

    /**
     * 由 login 视图发起的请求，用于登陆校验，如校验通过则将该用户的相关信息存入 Session 会话中
     *
     * @param genericLoginParams GenericLoginParams 对象中装了 rsa 解密后的登陆参数，可直接使用
     * @return 登录状态，用 ResponseObject 表示
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseObject login(GenericLoginParams genericLoginParams) {
        if (!StringUtils.isBlank(genericLoginParams.getUsername()) && !StringUtils.isBlank(genericLoginParams.getPassword())) {

            if (registerService.usernameIsLegal(genericLoginParams.getUsername()) && registerService.passwordIsLegal(genericLoginParams.getPassword())) {

                AccountInfo accountInfo = loginService.getAccountInfoByUsername(genericLoginParams.getUsername());

                if (accountInfo != null && loginService.loginSuccess(genericLoginParams.getPassword(), accountInfo.getPassword())) {

                    EncryptedInfo encryptedInfo = loginService.prepareData(genericLoginParams.getUsername());

                    StpUtil.login(accountInfo.getId(), true);


                    StpUtil.getSession().set(
                            "publicKey", encryptedInfo.getPublicKey()
                    ).set(
                            "privateKey", encryptedInfo.getPrivateSecret()
                    ).set(
                            "username", genericLoginParams.getUsername()
                    ).set(
                            "apiCanUseCount", accountInfo.getLongTermUsageCount() + accountInfo.getLimitedTermUsageCount()
                    ).set(
                            "longTermUsageCount", accountInfo.getLongTermUsageCount()
                    ).set(
                            "limitedTermUsageCount", accountInfo.getLimitedTermUsageCount()
                    ).set(
                            "limitedTermExpireTimes", accountInfo.getLimitedTermExpireTimes() != null ? accountInfo.getLimitedTermExpireTimes() : ""
                    ).set(
                            "apiUsedCount", accountInfo.getCallSuccessful()
                    ).set(
                            "accountId", accountInfo.getId()
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

    /**
     * 登陆页跳转到找回密码页
     *
     * @return 找回密码视图
     */
    @GetMapping("/forgot")
    public String jumpToForgetPage() {
        return "forgot.html";
    }

    /**
     * 找回密码页发起的请求，校验用户提供的邮箱合法时发送验证码至目标邮箱地址
     *
     * @param mail 用户邮箱地址
     * @return 发送状态，用 ProcessStatusEnum 表示
     * OK 表示发送成功
     * PARAM_EMPTY 表示参数为空
     * PARAM_NOT_SAFE 参数不安全，可能是前端没有使用特定的公钥进行加密或后端解密失败
     * MAIL_NOT_EXISTS 邮箱地址不存在或用户不存在
     * NO 表示发送失败
     */
    @PostMapping("/send")
    @ResponseBody
    public ProcessStatusEnum sendVerificationCode(@RequestParam(required = false) String mail) {

        if (StringUtils.isBlank(mail)) {
            return ProcessStatusEnum.PARAM_EMPTY;
        }

        try {
            mail = decrypt.rsaDecrypt(mail);
        } catch (Exception e) {
            e.printStackTrace();
            return ProcessStatusEnum.PARAM_NOT_SAFE;
        }

        AccountInfo accountInfoByEmail = loginService.getAccountInfoByEmail(mail);

        if (accountInfoByEmail == null || StringUtils.isBlank(accountInfoByEmail.getUsername())) {
            return ProcessStatusEnum.MAIL_NOT_EXISTS;
        }

        CreateMail createMail = loginService.prepareEmailBody(
                accountInfoByEmail.getUsername(),
                mail
        );

        boolean isSend = loginService.sendResetMail(createMail);

        if (isSend) {
            return ProcessStatusEnum.OK;
        }

        return ProcessStatusEnum.NO;
    }

    /**
     * 找回密码页发起的请求路由，携带了用户收到的邮箱验证码以及用户邮箱地址
     *
     * @param code 用户邮箱验证码
     * @param mail 用户邮箱地址
     * @return 处理状态，使用 ProcessStatusEnum 表示
     * OK 表示用户通过了校验
     * MAIL_NOT_EXISTS 表示用户提供的邮箱、用户不存在
     * VERIFICATION_FAILED 表示用户输入的验证码不正确
     * SERVER_ERROR 表示服务端异常
     */
    @PostMapping("/forgot")
    @ResponseBody
    public ProcessStatusEnum processResetPassword(@RequestParam(required = false) String code, @RequestParam(required = false) String mail) {

        try {
            mail = decrypt.rsaDecrypt(mail);
            code = decrypt.rsaDecrypt(code);
        } catch (Exception e) {
            e.printStackTrace();
            return ProcessStatusEnum.PARAM_NOT_SAFE;
        }

        AccountInfo accountInfoByEmail = loginService.getAccountInfoByEmail(mail);

        if (accountInfoByEmail == null || StringUtils.isBlank(accountInfoByEmail.getUsername())) {
            return ProcessStatusEnum.MAIL_NOT_EXISTS;
        }

        if (!loginService.checkVerificationCode(mail, code)) {
            return ProcessStatusEnum.VERIFICATION_FAILED;
        }

        CreateMail createMail = loginService.prepareResetMailBody(accountInfoByEmail.getUsername(), mail);

        boolean isSend = loginService.sendResetMail(createMail);

        if (!isSend) {
            return ProcessStatusEnum.SERVER_ERROR;
        }

        return ProcessStatusEnum.OK;
    }

    @GetMapping("/reset")
    public ModelAndView reset(@RequestParam(required = false) String uniqueToken, ModelAndView mav) {

        if (StringUtils.isBlank(uniqueToken) || uniqueToken.length() != 36) {
            throw new AccountPasswordResetException("FAILED::非法 token");
        }

        ProcessStatusEnum processStatusEnum = loginService.checkToken(uniqueToken);

        if (!processStatusEnum.equals(ProcessStatusEnum.OK)) {
            throw new AccountPasswordResetException("FAILED::非法 token");
        }

        mav.setViewName("reset.html");
        mav.addObject("isLogin", false);

        return mav;

    }

    @PostMapping("/reset")
    @ResponseBody
    public ProcessStatusEnum reset(ResetParams resetParams) {
        ProcessStatusEnum processStatusEnum = loginService.resetPasswordAtForget(resetParams);
        if (ProcessStatusEnum.SAME.equals(processStatusEnum)) {
            return ProcessStatusEnum.SAME;
        }
        if (ProcessStatusEnum.UNKNOWN.equals(processStatusEnum)) {
            return ProcessStatusEnum.UNKNOWN;
        }
        if (ProcessStatusEnum.NO.equals(processStatusEnum)) {
            return ProcessStatusEnum.NO;
        }
        return ProcessStatusEnum.OK;
    }

    /**
     * 登陆后 main 视图发起的请求，用户退出登陆，结束会话
     *
     * @return 处理状态，用 ResponseObject 表示
     */
    @GetMapping("/sessionLogOut")
    @ResponseBody
    public ResponseObject sessionLogout() {
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

    /**
     * 登陆后 main 视图发起的请求，这个请求仅跳转到 page 页面，不做数据处理
     *
     * @return 跳转至 page.html 页面
     */
    @GetMapping("/getApiDetails")
    public String jump() {
        return "page.html";
    }

    /**
     * 登陆后 page 视图发起的请求，配合 layui 框架的 table ajax 请求获取该用户所调用 API 的记录
     *
     * @param page  第几页，由 layui 提供
     * @param limit 所需条目数，由 layui 提供
     * @return Page 对象，封装了每次 ajax 的请求结果数据，包含处理状态以及数据、条目数
     */
    @GetMapping("/getPageList")
    @ResponseBody
    public Page getPageList(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer limit) {

        Page responsePage = new Page();

        PageInfo<PageInnerData> pageInfo = loginService.getPage(page, limit);
        AtomicInteger i = new AtomicInteger();
        if (pageInfo != null) {
            Long allCount = loginService.getAllCount();
            responsePage.setCount(allCount);
            responsePage.setMsg("SUCCESS::调用成功");
            responsePage.setCode(0);
            List<PageInnerData> pageInnerDataList = pageInfo.getList();
            pageInnerDataList.forEach(item -> item.setId(i.addAndGet(1)));
            responsePage.setData(pageInnerDataList);
        } else {
            responsePage.setMsg("FAILED::调用失败");
            responsePage.setCode(1);
        }


        return responsePage;
    }

    /**
     * @param mav
     * @return
     */
    @GetMapping("/change")
    public ModelAndView changePassword(ModelAndView mav) {
        mav.setViewName("reset.html");
        mav.addObject("rsaPublicKey", info.getRsaPublicKey());
        mav.addObject("isLogin", true);
        return mav;
    }

    @PostMapping("/change")
    @ResponseBody
    public ProcessStatusEnum resetPassword(ResetParams resetParams, HttpServletResponse httpServletResponse) {

        ProcessStatusEnum processStatusEnum = loginService.resetPasswordAtLogin(resetParams);

        if (ProcessStatusEnum.CURRENT_PASSWORD_INVALID.equals(processStatusEnum)) {
            // 密码不正确导致重置密码不成功
            return processStatusEnum;
        }

        if (ProcessStatusEnum.SAME.equals(processStatusEnum)) {
            return processStatusEnum;
        }

        if (ProcessStatusEnum.UNKNOWN.equals(processStatusEnum)) {
            // 未知原因导致重置密码不成功
            return processStatusEnum;
        }

        if (ProcessStatusEnum.OK.equals(processStatusEnum)) {
            // 重置密码成功，进行响应后关闭会话，这里要考虑到关闭会话后响应将无效
            loginService.sessionLogout();
            httpServletResponse.setHeader("X-Logout", "true");
            return ProcessStatusEnum.OK;
        }

        return ProcessStatusEnum.NO;


    }

    /**
     * @param mav
     * @return
     */
    @GetMapping("/main")
    public ModelAndView main(ModelAndView mav) {

        mav.addObject("publicKey", StpUtil.getSession().get("publicKey"));
        mav.addObject("privateKey", StpUtil.getSession().get("privateKey"));
        mav.addObject("username", StpUtil.getSession().get("username"));
        mav.addObject("apiCanUseCount", StpUtil.getSession().get("apiCanUseCount"));
        mav.addObject("apiUsedCount", StpUtil.getSession().get("apiUsedCount"));
        mav.addObject("limitedTermUsageCount", StpUtil.getSession().get("limitedTermUsageCount"));
        mav.addObject("longTermUsageCount", StpUtil.getSession().get("longTermUsageCount"));
        mav.addObject("limitedTermExpireTimes", StpUtil.getSession().get("limitedTermExpireTimes"));

        mav.setViewName("main");

        return mav;
    }
}

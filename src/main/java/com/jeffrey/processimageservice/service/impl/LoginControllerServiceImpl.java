package com.jeffrey.processimageservice.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jeffrey.processimageservice.entities.CreateMail;
import com.jeffrey.processimageservice.entities.Info;
import com.jeffrey.processimageservice.entities.ResetParams;
import com.jeffrey.processimageservice.entities.enums.ProcessStatusEnum;
import com.jeffrey.processimageservice.entities.sign.AccountInfo;
import com.jeffrey.processimageservice.entities.sign.EncryptedInfo;
import com.jeffrey.processimageservice.mapper.LoginControllerServiceImplMapper;
import com.jeffrey.processimageservice.service.LoginControllerService;
import com.jeffrey.processimageservice.utils.GenerateRandomStringUtil;
import com.jeffrey.processimageservice.vo.PageInnerData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class LoginControllerServiceImpl implements LoginControllerService {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private final LoginControllerServiceImplMapper loginControllerServiceImplMapper;
    private final BCryptPasswordEncoder encoder;
    private final JavaMailSender myJavaMailSender;
    private final MimeMessageHelper mimeMessageHelper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final Info info;
    private static final String FORGOT_MAIL_BODY = "<!DOCTYPE html><html lang=\"zh\"><head><meta charset=\"utf-8\"><title>密码重置邮件</title><style>*{box-sizing:border-box;margin:0;padding:0}body{font-family:Arial,sans-serif;font-size:14px;line-height:1.5;background-color:#f2f2f2}.container{width:100%;max-width:600px;margin:0 auto;padding:20px;background-color:#fff;border-radius:10px;box-shadow:0px 0px 10px rgba(0,0,0,0.3)}.code{font-size:32px;font-weight:bold;text-align:center;margin-bottom:30px;padding:20px;background-color:#f2f2f2;border-radius:10px;box-shadow:0px 0px 10px rgba(0,0,0,0.3)}p{margin-bottom:20px}.footer{margin-top:40px;text-align:center}</style></head><body><div class=\"container\"><p>您好 Username ，这封邮件的目的是告知您在 ResetTime 时进行了账户密码重置，本次重置密码需要验证码，如果确定操作是您本人行为请将以下验证码填写到找回密码页面中</p><p>验证码 15 分钟内有效，如果操作是您本人行为，请及时完成验证，如果您找不到页面可点击链接前往 <a href=\"QuickForgotPage\">重置页面</a> </p><div class=\"code\">VerificationCode</div><div class=\"footer\"><p>请在找回密码页面输入该验证码完成验证。</p><p>如果您没有申请找回密码，请忽略本邮件，并及时删除确保验证码不泄露。</p></div></div></body></html>";
    private static final String RESET_MAIL_BODY = "<!DOCTYPE html><html lang=\"zh\"><head><meta charset=\"utf-8\"><title>密码重置邮件</title><style>*{box-sizing:border-box;margin:0;padding:0}body{font-family:Arial,sans-serif;font-size:14px;line-height:1.5;background-color:#f2f2f2}.container{width:100%;max-width:600px;margin:0 auto;padding:20px;background-color:#fff;border-radius:10px;box-shadow:0px 0px 10px rgba(0,0,0,.3)}.link{font-size:32px;font-weight:bold;text-align:center;margin-bottom:30px;padding:20px;background-color:#f2f2f2;border-radius:10px;box-shadow:0px 0px 10px rgba(0,0,0,.3)}.link a{text-decoration:none}p{margin-bottom:20px}.footer{margin-top:40px;text-align:center}</style></head><body><div class=\"container\"><p>您好 <b>Username</b> ，这封邮件的目的是告知您在 <b>ResetTime</b> 时进行了账户密码重置，<b>目前已通过了验证码校验可直接修改密码</b>，如果确定操作是您本人行为请点击以下链接修改密码。</p><p>链接仅可访问一次且 24 小时内有效，如果操作是您本人行为请及时完成密码重置。</p><div class=\"link\"><a href=\"ResetLink\" target=\"_blank\">点击修改账户密码</a></div><div class=\"footer\"><p>点击链接后请在新的页面中完成密码重置。</p><p>如果您没有申请找回密码，请忽略本邮件，并及时删除确保重置链接不泄露。</p></div></div></body></html>";


    @Autowired
    public LoginControllerServiceImpl(LoginControllerServiceImplMapper loginControllerServiceImplMapper, BCryptPasswordEncoder encoder, JavaMailSender myJavaMailSender, MimeMessageHelper mimeMessageHelper, RedisTemplate<String, Object> redisTemplate, Info info) {
        this.loginControllerServiceImplMapper = loginControllerServiceImplMapper;
        this.encoder = encoder;
        this.myJavaMailSender = myJavaMailSender;
        this.mimeMessageHelper = mimeMessageHelper;
        this.redisTemplate = redisTemplate;
        this.info = info;
    }


    @Override
    public boolean loginSuccess(String pwd1, String pwd2) {

        return encoder.matches(pwd1, pwd2);
    }

    @Override
    public EncryptedInfo prepareData(String username) {
        return loginControllerServiceImplMapper.queryAuthorizationInformationDataByUserName(username);
    }

    @Override
    public AccountInfo getAccountInfoByUsername(String username) {
        return loginControllerServiceImplMapper.selectAccountInfoByUsername(username);
    }

    @Override
    public AccountInfo getAccountInfoByEmail(String email) {
        return loginControllerServiceImplMapper.selectAccountInfoByEmail(email);
    }

    @Override
    public PageInfo<PageInnerData> getPage(Integer page, Integer limit) {
        PageInfo<PageInnerData> pageInnerDataPageInfo;
        try {
            Integer aid = (Integer) StpUtil.getSession().getDataMap().get("accountId");
            PageHelper.startPage(page, limit);
            List<PageInnerData> pageList = loginControllerServiceImplMapper.getPage(aid);
            pageInnerDataPageInfo = new PageInfo<>(pageList);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return pageInnerDataPageInfo;
    }

    @Override
    public Long getAllCount() {
        return loginControllerServiceImplMapper.getAllCount((Integer) StpUtil.getSession().getDataMap().get("accountId"));
    }

    @Override
    public void sessionLogout() {
        StpUtil.logout();
    }

    @Override
    public ProcessStatusEnum resetPasswordAtLogin(ResetParams resetParams) {
        Object username = StpUtil.getSession().getDataMap().get("username");

        if (username == null) {
            return ProcessStatusEnum.UNKNOWN;
        }

        AccountInfo accountInfo = loginControllerServiceImplMapper.selectAccountInfoByUsername(((String) username));

        boolean currentPasswordIsInvalid = !encoder.matches(resetParams.getCurrentPassword(), accountInfo.getPassword());

        if (currentPasswordIsInvalid) {
            return ProcessStatusEnum.CURRENT_PASSWORD_INVALID;
        }

        if (encoder.matches(resetParams.getNewPassword(), accountInfo.getPassword())) {
            return ProcessStatusEnum.SAME;
        }
        String encodeNewPassword = encoder.encode(resetParams.getNewPassword());

        Integer result = loginControllerServiceImplMapper.updatePasswordByUsername(((String) username), encodeNewPassword);


        if (result == null || result == 0) {
            return ProcessStatusEnum.UNKNOWN;
        }

        return ProcessStatusEnum.OK;
    }

    @Override
    public ProcessStatusEnum resetPasswordAtForget(ResetParams resetParams) {

        String newPassword = resetParams.getNewPassword();
        String uniqueToken = resetParams.getUniqueToken();

        Object mail = redisTemplate.opsForHash().get(uniqueToken, "mail");

        if (mail == null) {
            return ProcessStatusEnum.UNKNOWN;
        }

        AccountInfo accountInfoByEmail = getAccountInfoByEmail(((String) mail));

        String encryptPassword = accountInfoByEmail.getPassword();

        boolean same = encoder.matches(newPassword, encryptPassword);

        if (same) {
            return ProcessStatusEnum.SAME;
        }

        newPassword = encoder.encode(newPassword);

        Integer result = loginControllerServiceImplMapper.updatePasswordByMail(((String) mail), newPassword);

        if (result == null || result == 0) {
            return ProcessStatusEnum.UNKNOWN;
        }

        return ProcessStatusEnum.OK;
    }

    @Override
    public boolean sendResetMail(CreateMail createMail) {
        try {
            mimeMessageHelper.setTo(createMail.getTargetLocation());
            mimeMessageHelper.setSubject(createMail.getSubject());
            mimeMessageHelper.setText(createMail.getContent(), true);
            myJavaMailSender.send(mimeMessageHelper.getMimeMessage());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CreateMail prepareEmailBody(String username, String mail) {
        String createTime = SDF.format(new Date(System.currentTimeMillis()));
        String randomStr = GenerateRandomStringUtil.createRandomStr(GenerateRandomStringUtil.CHAR_LOWER + GenerateRandomStringUtil.CHAR_UPPER + GenerateRandomStringUtil.NUMBER, 6);
        String body = FORGOT_MAIL_BODY
                .replace("Username", username)
                .replace("ResetTime", createTime)
                .replace("VerificationCode", randomStr)
                .replace("QuickForgotPage", info.getServerDomain() + "/user/forgot");
        redisTemplate.opsForValue().set(mail, randomStr, 15, TimeUnit.MINUTES);
        return new CreateMail(
                createTime, username, mail, "密码重置邮件", body, randomStr
        );

    }

    @Override
    public CreateMail prepareResetMailBody(String username, String mail) {
        String createTime = SDF.format(new Date(System.currentTimeMillis()));
        String resetLink = this.prepareTemporaryLink(username, mail);
        String body = RESET_MAIL_BODY
                .replace("Username", username)
                .replace("ResetTime", createTime)
                .replace("ResetLink", resetLink);
        return new CreateMail(
                createTime, username, mail, "可重置密码邮件", body, null
        );
    }

    @Override
    public boolean checkVerificationCode(String mail, String code) {

        if (StringUtils.isBlank(mail) || StringUtils.isBlank(code)) {
            return false;
        }

        Boolean hasMail = redisTemplate.hasKey(mail);

        if (hasMail == null || !hasMail) {
            return false;
        }

        Object getCode = redisTemplate.opsForValue().get(mail);

        if (getCode == null) {
            return false;
        }

        redisTemplate.delete(mail);

        return getCode.equals(code);
    }

    private String prepareTemporaryLink(String username, String mail) {
        String token = UUID.randomUUID().toString();
        HashMap<String, Object> map = new HashMap<>(3);
        map.put("isFirstAccess", true);
        map.put("username", username);
        map.put("mail", mail);
        redisTemplate.opsForHash().putAll(token, map);
        redisTemplate.expire(token, 24, TimeUnit.HOURS);
        return String.format("%s/user/reset?uniqueToken=%s", info.getServerDomain(), token);
    }

    @Override
    public ProcessStatusEnum checkToken(String token) {

        Boolean hasToken = redisTemplate.hasKey(token);
        if (hasToken == null || !hasToken) {
            return ProcessStatusEnum.TOKEN_INVALID_OR_NOT_EXIST;
        }
        Object value = redisTemplate.opsForHash().get(token, "isFirstAccess");

        if (value == null) {
            return ProcessStatusEnum.TOKEN_INVALID_OR_NOT_EXIST;
        }

        if ((Boolean) value) {
            redisTemplate.opsForHash().put(token, "isFirstAccess", false);
            return ProcessStatusEnum.OK;
        } else {
            return ProcessStatusEnum.TOKEN_NOT_FIRST_ACCESS;
        }

    }
}

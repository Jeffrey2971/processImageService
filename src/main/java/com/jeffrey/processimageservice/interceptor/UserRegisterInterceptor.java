package com.jeffrey.processimageservice.interceptor;

import com.jeffrey.processimageservice.entities.FailedItem;
import com.jeffrey.processimageservice.exception.exception.clitent.RegisterException;
import com.jeffrey.processimageservice.security.Decrypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class UserRegisterInterceptor implements HandlerInterceptor {

    private final Decrypt decrypt;

    @Autowired
    public UserRegisterInterceptor(Decrypt decrypt) {
        this.decrypt = decrypt;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        ArrayList<FailedItem> failedItemsList = new ArrayList<>(4);

        boolean exception = false;

        String username = decrypt.rsaDecrypt(request.getParameter("username"));


        if (StringUtils.isBlank(username) || !Pattern.compile("^[a-zA-Z0-9_-]{6,16}$").matcher(username).matches()) {
            // 8-16 位字母、数字、下划线、减号
            failedItemsList.add(new FailedItem("username", "用户名格式错误，由 8-16 位字母、数字、下划线、减号组成"));
            exception = true;
        }

        String password = decrypt.rsaDecrypt(request.getParameter("password"));

        if (StringUtils.isBlank(password) || !Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,16}$").matcher(password).matches()) {
            // 必须包含大小写字母和数字的组合，可以使用特殊字符，长度在8-10之间
            failedItemsList.add(new FailedItem("password", "密码格式有误，密码必须包含大小写字母和数字的组合，可以使用特殊字符，长度在8-10之间"));
            exception = true;
        }

        String ensurePassword = decrypt.rsaDecrypt(request.getParameter("ensure"));

        if (StringUtils.isBlank(ensurePassword) || !password.equals(ensurePassword)) {
            failedItemsList.add(new FailedItem("ensurePassword", "两次密码不一致"));
            exception = true;
        }

        String email = decrypt.rsaDecrypt(request.getParameter("email"));

        if (StringUtils.isBlank(email) || !Pattern.compile("^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$").matcher(email).matches()) {
            failedItemsList.add(new FailedItem("email", "邮箱格式有误"));
            exception = true;
        }

        if (exception) {
            RegisterException registerException = new RegisterException("服务端注册参数校验失败");
            registerException.setFailedItems(failedItemsList);
            throw registerException;
        }

        return true;
    }
}

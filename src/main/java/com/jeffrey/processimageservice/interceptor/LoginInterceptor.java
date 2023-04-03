package com.jeffrey.processimageservice.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            StpUtil.checkLogin();
        } catch (Exception e) {
            response.sendRedirect("/watermark/user/login?msg=login_required");
            return false;
        }
        return true;
    }
}

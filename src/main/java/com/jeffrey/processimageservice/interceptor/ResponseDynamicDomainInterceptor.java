package com.jeffrey.processimageservice.interceptor;

import com.jeffrey.processimageservice.entities.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class ResponseDynamicDomainInterceptor implements HandlerInterceptor {
    private final Info info;

    @Autowired
    public ResponseDynamicDomainInterceptor(Info info) {
        this.info = info;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String serverDomain = info.getServerDomain();
        request.setAttribute("dynamicDomain", serverDomain);
        request.setAttribute("rsaPublicKey", info.getRsaPublicKey());
        return true;
    }


}

package com.jeffrey.processimageservice.filter;

import com.google.gson.Gson;
import com.jeffrey.processimageservice.entities.response.Data;
import com.jeffrey.processimageservice.entities.response.GenericResponse;
import com.jeffrey.processimageservice.listener.ShutdownHook;
import com.jeffrey.processimageservice.utils.GetRequestAddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class RequestFilter implements Filter {

    private final ShutdownHook shutdownHook;

    @Autowired
    public RequestFilter(ShutdownHook shutdownHook) {
        this.shutdownHook = shutdownHook;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (shutdownHook.serverIsShuttingDown()) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String ipAddress = GetRequestAddressUtil.getIPAddress(httpServletRequest);
            log.warn("拒绝关闭期间请求：{}", ipAddress);
            GenericResponse genericResponse = new GenericResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "SHUTTING::服务正在停止", "json", null, null, "ExceptionHandler[ServiceShuttingDownException.class]", ipAddress, new Data(52001, null, "UNHANDLED::因其他问题导致未处理", null, null));
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(genericResponse));
        }else {
            chain.doFilter(request, response);
        }
    }
}

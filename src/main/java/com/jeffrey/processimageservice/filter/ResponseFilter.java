package com.jeffrey.processimageservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
@Slf4j
public class ResponseFilter implements Filter {

    /**
     * 开启同源 iframe 许可
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
        chain.doFilter(request, response);

    }
}

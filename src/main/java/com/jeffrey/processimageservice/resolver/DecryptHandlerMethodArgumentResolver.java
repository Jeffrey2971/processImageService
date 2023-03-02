package com.jeffrey.processimageservice.resolver;

import com.jeffrey.processimageservice.entities.ResetParams;
import com.jeffrey.processimageservice.entities.register.GenericLoginParams;
import com.jeffrey.processimageservice.entities.register.RegisterParams;
import com.jeffrey.processimageservice.security.Decrypt;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class DecryptHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final Decrypt decrypt;

    @Autowired
    public DecryptHandlerMethodArgumentResolver(Decrypt decrypt) {
        this.decrypt = decrypt;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return
                parameter.getParameterType().equals(RegisterParams.class)
                        || parameter.getParameterType().equals(GenericLoginParams.class)
                        || parameter.getParameterType().equals(ResetParams.class);
    }

    @Override
    public Object resolveArgument(
            @NotNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();

        if (parameter.getParameterType().equals(RegisterParams.class)) {
            return new RegisterParams(
                    decrypt.rsaDecrypt(httpServletRequest.getParameter("username")),
                    decrypt.rsaDecrypt(httpServletRequest.getParameter("password")),
                    decrypt.rsaDecrypt(httpServletRequest.getParameter("ensure")),
                    decrypt.rsaDecrypt(httpServletRequest.getParameter("email"))
            );
        } else if (parameter.getParameterType().equals(GenericLoginParams.class)) {
            return new GenericLoginParams(
                    decrypt.rsaDecrypt(httpServletRequest.getParameter("username")),
                    decrypt.rsaDecrypt(httpServletRequest.getParameter("password"))
            );
        } else if (parameter.getParameterType().equals(ResetParams.class)) {
            return new ResetParams(
                    decrypt.rsaDecrypt(httpServletRequest.getParameter("newPassword")),
                    decrypt.rsaDecrypt(httpServletRequest.getParameter("currentPassword")),
                    decrypt.rsaDecrypt(httpServletRequest.getParameter("uniqueToken"))
            );
        }
        return null;

    }
}

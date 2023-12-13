package com.jeffrey.processimageservice.conf;

import com.jeffrey.processimageservice.ProcessImageServiceApplication;
import com.jeffrey.processimageservice.interceptor.*;
import com.jeffrey.processimageservice.resolver.DecryptHandlerMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.ParameterContentNegotiationStrategy;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class WebConfig implements WebMvcConfigurer {

    private final SignatureVerificationInterceptor signatureVerificationInterceptor;
    private final DecryptHandlerMethodArgumentResolver decryptHandlerMethodArgumentResolver;

    private final CheckRequestParamsInterceptor checkRequestParamsInterceptor;

    private final UserRegisterInterceptor userRegisterInterceptor;

    private final ResponseWrapperInterceptor responseWrapperInterceptor;
    private final LoginInterceptor loginInterceptor;

    @Autowired
    public WebConfig(SignatureVerificationInterceptor signatureVerificationInterceptor, DecryptHandlerMethodArgumentResolver decryptHandlerMethodArgumentResolver, CheckRequestParamsInterceptor checkRequestParamsInterceptor, UserRegisterInterceptor userRegisterInterceptor, ResponseWrapperInterceptor responseWrapperInterceptor, LoginInterceptor loginInterceptor) {
        this.signatureVerificationInterceptor = signatureVerificationInterceptor;
        this.decryptHandlerMethodArgumentResolver = decryptHandlerMethodArgumentResolver;
        this.checkRequestParamsInterceptor = checkRequestParamsInterceptor;
        this.userRegisterInterceptor = userRegisterInterceptor;
        this.responseWrapperInterceptor = responseWrapperInterceptor;
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/access");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/targetImages/**")
                .addResourceLocations(String.format("file:%s/", ProcessImageServiceApplication.getTargetImage()))
                .setCacheControl(CacheControl.maxAge(183, TimeUnit.DAYS)
                        .cachePrivate())
                .setCachePeriod(183 * (24 * 60 * 60));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/user/**", "/order/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/status",
                        "/user/register/**",
                        "/user/forgot/**",
                        "/user/send/**",
                        "/user/reset/**"
                );
        registry.addInterceptor(signatureVerificationInterceptor).addPathPatterns("/access/**").excludePathPatterns("/access/demo-preview/**", "/register/**");
        registry.addInterceptor(checkRequestParamsInterceptor).addPathPatterns("/access/**").excludePathPatterns("/access/demo-preview/**", "/register/**");
        registry.addInterceptor(userRegisterInterceptor).addPathPatterns("/register/**").excludePathPatterns("/access/demo-preview/**", "/access/**", "/register/async-check/**");
        registry.addInterceptor(responseWrapperInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(decryptHandlerMethodArgumentResolver);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

        Map<String, MediaType> mediaType = new HashMap<>();
        mediaType.put("json", MediaType.APPLICATION_JSON);
        mediaType.put("xml", MediaType.APPLICATION_XML);
        mediaType.put("x-jeffrey", MediaType.parseMediaType("application/x-jeffrey"));

        ParameterContentNegotiationStrategy parameterContentNegotiationStrategy = new ParameterContentNegotiationStrategy(mediaType);
        HeaderContentNegotiationStrategy headerContentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        configurer.strategies(Arrays.asList(parameterContentNegotiationStrategy, headerContentNegotiationStrategy));
    }
}

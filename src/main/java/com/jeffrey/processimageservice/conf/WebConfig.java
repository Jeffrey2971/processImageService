package com.jeffrey.processimageservice.conf;

import com.jeffrey.processimageservice.ProcessImageServiceApplication;
import com.jeffrey.processimageservice.interceptor.CheckRequestParams;
import com.jeffrey.processimageservice.interceptor.SignatureVerificationInterceptor;
import com.jeffrey.processimageservice.interceptor.UseCounterInterceptor;
import com.jeffrey.processimageservice.interceptor.UserRegisterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.accept.ParameterContentNegotiationStrategy;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Component
public class WebConfig implements WebMvcConfigurer {

    private final UseCounterInterceptor useCounterInterceptor;
    private final SignatureVerificationInterceptor signatureVerificationInterceptor;
    private final UserRegisterInterceptor userRegisterInterceptor;

    private final CheckRequestParams checkRequestParams;



    @Autowired
    public WebConfig(UseCounterInterceptor useCounterInterceptor, SignatureVerificationInterceptor signatureVerificationInterceptor, UserRegisterInterceptor userRegisterInterceptor, CheckRequestParams checkRequestParams) {
        this.useCounterInterceptor = useCounterInterceptor;
        this.signatureVerificationInterceptor = signatureVerificationInterceptor;
        this.userRegisterInterceptor = userRegisterInterceptor;
        this.checkRequestParams = checkRequestParams;
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
        /// registry.addInterceptor(useCounterInterceptor);
        registry.addInterceptor(checkRequestParams).addPathPatterns("/single-upload/**").order(1);
        registry.addInterceptor(signatureVerificationInterceptor).addPathPatterns("/single-upload/**").order(2);
        // registry.addInterceptor(userRegisterInterceptor);

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

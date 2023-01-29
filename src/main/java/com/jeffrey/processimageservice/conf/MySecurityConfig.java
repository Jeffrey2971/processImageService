package com.jeffrey.processimageservice.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * @author jeffrey
 * @since JDK 1.8
 */

@Configuration
public class MySecurityConfig extends WebSecurityConfigurerAdapter {
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests().anyRequest().permitAll().and().logout()
//                .permitAll().and().csrf().disable();
////        http.authorizeRequests().anyRequest().authenticated().and().formLogin().loginPage()
////                .antMatcher("/register/**")
////                .csrf().csrfTokenRepository(csrfTokenRepository());
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll()
                .and().csrf().disable();
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
        httpSessionCsrfTokenRepository.setHeaderName("X-JEFFRET-XSRF-TOKEN");
        return httpSessionCsrfTokenRepository;
    }
}

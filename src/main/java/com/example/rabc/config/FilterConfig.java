package com.example.rabc.config;

import com.example.rabc.filter.AuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterRegistration(){
        FilterRegistrationBean<AuthFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new AuthFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(1);
        return bean;
    }
}

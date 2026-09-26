package com.example.rabc.config;

import com.example.rabc.filter.AuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    //注入spring管理AuthFilter
    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterFilterRegistrationBean
            (AuthFilter authFilter){
        FilterRegistrationBean<AuthFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(authFilter);
        bean.addUrlPatterns("/*");
        bean.setOrder(1);
        return bean;
    }
}

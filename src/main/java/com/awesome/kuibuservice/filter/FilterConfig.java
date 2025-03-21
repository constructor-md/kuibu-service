package com.awesome.kuibuservice.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<HttpCacheFilter> cachingFilter() {
        FilterRegistrationBean<HttpCacheFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new HttpCacheFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}

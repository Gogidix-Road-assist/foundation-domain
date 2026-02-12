package com.gogidix.rapidassist.shared.request.context.library.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(RequestContextProperties.class)
public class SharedRequestContextAutoConfiguration {

    @Bean
    public FilterRegistrationBean<RequestContextFilter> gogidixRequestContextFilter(RequestContextProperties properties) {
        FilterRegistrationBean<RequestContextFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestContextFilter(properties));
        registrationBean.setOrder(-100);
        return registrationBean;
    }
}

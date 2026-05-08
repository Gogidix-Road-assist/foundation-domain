package com.gogidix.rapidassist.shared.request.context.library.autoconfigure;

import com.gogidix.rapidassist.shared.request.context.library.constant.TenantConstants;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for shared request context library.
 *
 * <p>This configuration class automatically configures the RequestContextFilter
 * for servlet-based web applications.</p>
 *
 * @author Rapid Assist
 * @version 1.0.0
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(RequestContextProperties.class)
public class SharedRequestContextAutoConfiguration {

    /**
     * Configures the RequestContextFilter as a servlet filter.
     *
     * @param properties the configuration properties for the request context
     * @return the filter registration bean
     */
    @Bean
    public FilterRegistrationBean<RequestContextFilter> gogidixRequestContextFilter(
            final RequestContextProperties properties) {
        FilterRegistrationBean<RequestContextFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestContextFilter(properties));
        registrationBean.setOrder(TenantConstants.FILTER_ORDER_DEFAULT);
        return registrationBean;
    }
}

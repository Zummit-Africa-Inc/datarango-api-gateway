package com.datarango.gateway.config;

import com.datarango.gateway.middleware.LoggingFilter;
import com.datarango.gateway.middleware.RateLimitFilter;
import com.datarango.gateway.service.MicroserviceClient;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class FilterConfig {

    @Bean
    public RateLimitFilter rateLimitFilter(RedisTemplate<String, String> redisTemplate, MicroserviceClient microserviceClient) {
        return new RateLimitFilter(redisTemplate, microserviceClient);
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration(RateLimitFilter rateLimitFilter) {
        FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(rateLimitFilter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilterRegistration(LoggingFilter loggingFilter) {
        FilterRegistrationBean<LoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(loggingFilter);
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        return registration;
    }
}
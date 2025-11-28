package com.datarango.gateway.middleware;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        long startTime = System.currentTimeMillis();
        
        logger.info("Request: {} {} from {}", 
            httpRequest.getMethod(), 
            httpRequest.getRequestURI(), 
            httpRequest.getRemoteAddr());
        
        chain.doFilter(request, response);
        
        long duration = System.currentTimeMillis() - startTime;
        
        logger.info("Response: {} {} - Status: {} - Duration: {}ms", 
            httpRequest.getMethod(), 
            httpRequest.getRequestURI(), 
            httpResponse.getStatus(), 
            duration);
    }
}
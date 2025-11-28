package com.datarango.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "microservices.urls")
public class MicroserviceProperties {
    private String userService;
    private String coreService;
    private String streamingService;
    private String messagingService;
}
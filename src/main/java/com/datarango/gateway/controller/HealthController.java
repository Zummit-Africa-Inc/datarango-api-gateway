package com.datarango.gateway.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.datarango.gateway.dto.ApiResponse;

@RestController
public class HealthController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${microservices.urls.user-service}")
    private String userServiceUrl;

    @Value("${microservices.urls.core-service}")
    private String coreServiceUrl;

    @Value("${microservices.urls.streaming-service}")
    private String streamingServiceUrl;

    @Value("${microservices.urls.messaging-service}")
    private String messagingServiceUrl;

    @GetMapping("/")
    public ApiResponse<Object> welcome() {
        return ApiResponse.success("Welcome to the Datarango API Gateway", null);
    }

    @GetMapping("/health")
    public ApiResponse<Object> health() {
        return ApiResponse.success("API Gateway is healthy", null);
    }

    @GetMapping("/health/user")
    public ApiResponse<Object> health_user() {
        return checkService(userServiceUrl + "/health", "User service");
    }

    @GetMapping("/health/core")
    public ApiResponse<Object> health_core() {
        return checkService(coreServiceUrl + "/health", "Core service");
    }

    @GetMapping("/health/streaming")
    public ApiResponse<Object> health_streaming() {
        return checkService(streamingServiceUrl + "/health", "Streaming service");
    }

    @GetMapping("/health/messaging")
    public ApiResponse<Object> health_messaging() {
        return checkService(messagingServiceUrl + "/health", "Messaging service");
    }

    private ApiResponse<Object> checkService(String url, String serviceName) {
        try {
            if (url != null) {
                restTemplate.getForObject(url, String.class);
                return ApiResponse.success(serviceName + " is healthy", null);
            } else {
                return new ApiResponse<>(false, serviceName + " is not available", null, "Service URL is null", 404);
            }
        } catch (Exception e) {
            String errorMessage = Objects.requireNonNullElse(e.getMessage(), "Unknown error");
            return new ApiResponse<>(false, serviceName + " is not available", null, errorMessage, 404);
        }
    }
}
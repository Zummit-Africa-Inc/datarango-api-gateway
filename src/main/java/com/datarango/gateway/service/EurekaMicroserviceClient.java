package com.datarango.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.lang.Nullable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EurekaMicroserviceClient {

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    @Value("${microservices.urls.user-service:}")
    private String userServiceUrl;

    @Value("${microservices.urls.core-service:}")
    private String coreServiceUrl;

    @Value("${microservices.urls.streaming-service:}")
    private String streamingServiceUrl;

    @Value("${microservices.urls.messaging-service:}")
    private String messagingServiceUrl;

    public <T> ResponseEntity<T> callUserService(String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        return callService("user-service", userServiceUrl, endpoint, method, body, responseType);
    }

    public <T> ResponseEntity<T> callCoreService(String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        return callService("core-service", coreServiceUrl, endpoint, method, body, responseType);
    }

    public <T> ResponseEntity<T> callStreamingService(String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        return callService("streaming-service", streamingServiceUrl, endpoint, method, body, responseType);
    }

    public <T> ResponseEntity<T> callMessagingService(String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        return callService("messaging-service", messagingServiceUrl, endpoint, method, body, responseType);
    }

    private <T> ResponseEntity<T> callService(String serviceName, String fallbackUrl, String endpoint,
            HttpMethod method, @Nullable Object body,
            Class<T> responseType) {
        String serviceUrl = null;
        try {
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
            if (instances != null && !instances.isEmpty()) {
                serviceUrl = instances.get(0).getUri().toString();
            }
        } catch (Exception e) {
            // Discovery failed, proceed to fallback
        }

        if (serviceUrl == null) {
            if (fallbackUrl != null && !fallbackUrl.isEmpty()) {
                serviceUrl = fallbackUrl;
            } else {
                throw new IllegalStateException("Service URL not found for " + serviceName);
            }
        }

        if (method == null) {
            throw new IllegalArgumentException("HttpMethod cannot be null");
        }

        if (responseType == null) {
            throw new IllegalArgumentException("Response type cannot be null");
        }

        HttpEntity<?> httpEntity = body != null ? new HttpEntity<>(body) : new HttpEntity<>(new HttpHeaders());
        return restTemplate.exchange(serviceUrl + endpoint, method, httpEntity, responseType);
    }
}
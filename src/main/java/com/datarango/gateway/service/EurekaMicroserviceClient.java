package com.datarango.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class EurekaMicroserviceClient {

    private final RestTemplate restTemplate;
    private final DiscoveryClient discoveryClient;

    public <T> ResponseEntity<T> callUserService(String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        return callService("user-service", endpoint, method, body, responseType);
    }

    public <T> ResponseEntity<T> callCoreService(String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        return callService("core-service", endpoint, method, body, responseType);
    }

    public <T> ResponseEntity<T> callStreamingService(String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        return callService("streaming-service", endpoint, method, body, responseType);
    }

    public <T> ResponseEntity<T> callMessagingService(String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        return callService("messaging-service", endpoint, method, body, responseType);
    }

    private <T> ResponseEntity<T> callService(String serviceName, String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        String serviceUrl = discoveryClient.getInstances(serviceName).get(0).getUri().toString();
        return restTemplate.exchange(serviceUrl + endpoint, method, new HttpEntity<>(body), responseType);
    }
}
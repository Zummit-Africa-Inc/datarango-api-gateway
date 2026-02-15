package com.datarango.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class EurekaMicroserviceClient {

    private final RestTemplate restTemplate;

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
        if (method == null) {
            throw new IllegalArgumentException("HTTP method cannot be null");
        }
        if (responseType == null) {
            throw new IllegalArgumentException("Response type cannot be null");
        }

        String serviceUrl = "http://" + serviceName + endpoint;
        log.debug("Calling service: {} with method: {}", serviceUrl, method);

        HttpEntity<?> requestEntity = body != null ? new HttpEntity<>(body) : HttpEntity.EMPTY;
        return restTemplate.exchange(serviceUrl, method, requestEntity, responseType);
    }
}
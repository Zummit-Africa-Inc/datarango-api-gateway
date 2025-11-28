package com.datarango.gateway.service;

import com.datarango.gateway.config.MicroserviceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MicroserviceClient {
    
    private final RestTemplate restTemplate;
    private final MicroserviceProperties microserviceProperties;
    
    public <T> ResponseEntity<T> callUserService(String endpoint, HttpMethod method, Object body, Class<T> responseType) {
        return callService(microserviceProperties.getUserService(), endpoint, method, body, responseType);
    }
    
    public <T> ResponseEntity<T> callCoreService(String endpoint, HttpMethod method, Object body, Class<T> responseType) {
        return callService(microserviceProperties.getCoreService(), endpoint, method, body, responseType);
    }
    
    public <T> ResponseEntity<T> callStreamingService(String endpoint, HttpMethod method, Object body, Class<T> responseType) {
        return callService(microserviceProperties.getStreamingService(), endpoint, method, body, responseType);
    }
    
    public <T> ResponseEntity<T> callMessagingService(String endpoint, HttpMethod method, Object body, Class<T> responseType) {
        return callService(microserviceProperties.getMessagingService(), endpoint, method, body, responseType);
    }
    
    private <T> ResponseEntity<T> callService(String baseUrl, String endpoint, HttpMethod method, Object body, Class<T> responseType) {
        String url = baseUrl + endpoint;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        HttpMethod httpMethod = method != null ? method : HttpMethod.GET;
        if (httpMethod != null && responseType != null) {
            return restTemplate.exchange(url, httpMethod, entity, responseType);
        }
        throw new IllegalArgumentException("Response type cannot be null");
    }
}
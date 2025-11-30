package com.datarango.gateway.service;

import com.datarango.gateway.config.MicroserviceProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
@RequiredArgsConstructor
public class MicroserviceClient {

    private final RestTemplate restTemplate;
    private final MicroserviceProperties microserviceProperties;

    public <T> ResponseEntity<T> callUserService(String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        return callService(microserviceProperties.getUserService(), endpoint, method, body, responseType);
    }

    public <T> ResponseEntity<T> callCoreService(String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        return callService(microserviceProperties.getCoreService(), endpoint, method, body, responseType);
    }

    public <T> ResponseEntity<T> callStreamingService(String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        return callService(microserviceProperties.getStreamingService(), endpoint, method, body, responseType);
    }

    public <T> ResponseEntity<T> callMessagingService(String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        return callService(microserviceProperties.getMessagingService(), endpoint, method, body, responseType);
    }

    private <T> ResponseEntity<T> callService(String baseUrl, String endpoint, HttpMethod method, Object body,
            Class<T> responseType) {
        if (responseType == null) {
            throw new IllegalArgumentException("Response type cannot be null");
        }

        String url = baseUrl + endpoint;
        HttpHeaders headers = buildHeaders();
        HttpMethod httpMethod = method != null ? method : HttpMethod.GET;
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        if (httpMethod == null) {
            throw new IllegalArgumentException("HTTP method cannot be null");
        }

        try {
            log.debug("Calling service: {} {} {}", httpMethod, url, body != null ? "with body" : "");
            ResponseEntity<T> response = restTemplate.exchange(url, httpMethod, entity, responseType);
            log.debug("Service call successful: {} - Status: {}", url, response.getStatusCode());
            return response;
        } catch (HttpClientErrorException e) {
            log.error("Client error calling {}: {} - {}", url, e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode())
                    .body(null);
        } catch (HttpServerErrorException e) {
            log.error("Server error calling {}: {} - {}", url, e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(null);
        } catch (ResourceAccessException e) {
            log.error("Timeout or connection error calling {}: {}", url, e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(null);
        } catch (Exception e) {
            log.error("Unexpected error calling {}: {}", url, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            if (request != null) {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && !authHeader.isEmpty()) {
                    headers.set("Authorization", authHeader);
                }

                String correlationId = request.getHeader("X-Correlation-ID");
                if (correlationId == null || correlationId.isEmpty()) {
                    correlationId = java.util.UUID.randomUUID().toString();
                }
                if (correlationId != null && !correlationId.isEmpty()) {
                    headers.set("X-Correlation-ID", correlationId);
                }

                String userAgent = request.getHeader("User-Agent");
                if (userAgent != null && !userAgent.isEmpty()) {
                    headers.set("X-Forwarded-User-Agent", userAgent);
                }

                String clientIp = getClientIp(request);
                if (clientIp != null && !clientIp.isEmpty()) {
                    headers.set("X-Forwarded-For", clientIp);
                }
            }
        }

        return headers;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
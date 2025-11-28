package com.datarango.gateway.controller;

import com.datarango.gateway.service.MicroserviceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final MicroserviceClient microserviceClient;

    @RequestMapping(value = "/api/users/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE })
    public ResponseEntity<String> routeToUserService(HttpServletRequest request,
            @RequestBody(required = false) Object body) {
        String endpoint = request.getRequestURI().replace("/api/users", "");
        String method = request.getMethod();
        return microserviceClient.callUserService(endpoint,
                method != null ? HttpMethod.valueOf(method) : HttpMethod.GET, body, String.class);
    }

    @RequestMapping(value = "/api/core/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE })
    public ResponseEntity<String> routeToCoreService(HttpServletRequest request,
            @RequestBody(required = false) Object body) {
        String endpoint = request.getRequestURI().replace("/api/core", "");
        String method = request.getMethod();
        return microserviceClient.callCoreService(endpoint,
                method != null ? HttpMethod.valueOf(method) : HttpMethod.GET, body, String.class);
    }

    @RequestMapping(value = "/api/streaming/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE })
    public ResponseEntity<String> routeToStreamingService(HttpServletRequest request,
            @RequestBody(required = false) Object body) {
        String endpoint = request.getRequestURI().replace("/api/streaming", "");
        String method = request.getMethod();
        return microserviceClient.callStreamingService(endpoint,
                method != null ? HttpMethod.valueOf(method) : HttpMethod.GET, body, String.class);
    }

    @RequestMapping(value = "/api/messaging/**", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE })
    public ResponseEntity<String> routeToMessagingService(HttpServletRequest request,
            @RequestBody(required = false) Object body) {
        String endpoint = request.getRequestURI().replace("/api/messaging", "");
        String method = request.getMethod();
        return microserviceClient.callMessagingService(endpoint,
                method != null ? HttpMethod.valueOf(method) : HttpMethod.GET, body, String.class);
    }
}
package com.datarango.gateway.controller;

import com.datarango.gateway.dto.ApiResponse;
import com.datarango.gateway.exception.BadRequestException;
import com.datarango.gateway.exception.ForbiddenException;
import com.datarango.gateway.exception.NotFoundException;
import com.datarango.gateway.exception.ServiceUnavailableException;
import com.datarango.gateway.exception.UnauthorizedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-errors")
public class ErrorTestController {

    @GetMapping("/not-found")
    public ApiResponse<Object> testNotFound() {
        throw new NotFoundException("This is a test 404 error");
    }

    @GetMapping("/bad-request")
    public ApiResponse<Object> testBadRequest() {
        throw new BadRequestException("This is a test 400 error");
    }

    @GetMapping("/unauthorized")
    public ApiResponse<Object> testUnauthorized() {
        throw new UnauthorizedException("This is a test 401 error");
    }

    @GetMapping("/forbidden")
    public ApiResponse<Object> testForbidden() {
        throw new ForbiddenException("This is a test 403 error");
    }

    @GetMapping("/service-unavailable")
    public ApiResponse<Object> testServiceUnavailable() {
        throw new ServiceUnavailableException("This is a test 503 error");
    }

    @GetMapping("/internal-error")
    public ApiResponse<Object> testInternalError() {
        throw new RuntimeException("This is a test 500 error");
    }

    @GetMapping("/illegal-argument")
    public ApiResponse<Object> testIllegalArgument() {
        throw new IllegalArgumentException("This is a test illegal argument error");
    }
}

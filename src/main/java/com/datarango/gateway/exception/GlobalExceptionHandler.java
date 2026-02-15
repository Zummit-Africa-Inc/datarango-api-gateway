package com.datarango.gateway.exception;

import com.datarango.gateway.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleNotFoundException(NotFoundException ex, WebRequest request) {
                log.error("Resource not found: {}", ex.getMessage());
                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                "Resource not found",
                                null,
                                ex.getMessage(),
                                HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException ex,
                        WebRequest request) {
                log.error("Bad request: {}", ex.getMessage());
                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                "Bad request",
                                null,
                                ex.getMessage(),
                                HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ApiResponse<Object>> handleUnauthorizedException(UnauthorizedException ex,
                        WebRequest request) {
                log.error("Unauthorized access: {}", ex.getMessage());
                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                "Unauthorized",
                                null,
                                ex.getMessage(),
                                HttpStatus.UNAUTHORIZED.value());
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<ApiResponse<Object>> handleForbiddenException(ForbiddenException ex, WebRequest request) {
                log.error("Forbidden access: {}", ex.getMessage());
                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                "Forbidden",
                                null,
                                ex.getMessage(),
                                HttpStatus.FORBIDDEN.value());
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(ServiceUnavailableException.class)
        public ResponseEntity<ApiResponse<Object>> handleServiceUnavailableException(ServiceUnavailableException ex,
                        WebRequest request) {
                log.error("Service unavailable: {}", ex.getMessage());
                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                "Service unavailable",
                                null,
                                ex.getMessage(),
                                HttpStatus.SERVICE_UNAVAILABLE.value());
                return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }

        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleNoHandlerFoundException(NoHandlerFoundException ex,
                        WebRequest request) {
                log.error("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());
                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                "Endpoint not found",
                                null,
                                String.format("No endpoint found for %s %s", ex.getHttpMethod(), ex.getRequestURL()),
                                HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException ex,
                        WebRequest request) {
                log.error("No resource found: {}", ex.getMessage());
                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                "Resource not found",
                                null,
                                "The requested resource was not found",
                                HttpStatus.NOT_FOUND.value());
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
                        MethodArgumentNotValidException ex) {
                log.error("Validation error: {}", ex.getMessage());
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getAllErrors().forEach((error) -> {
                        String fieldName = ((FieldError) error).getField();
                        String errorMessage = error.getDefaultMessage();
                        errors.put(fieldName, errorMessage);
                });

                ApiResponse<Map<String, String>> response = new ApiResponse<>(
                                false,
                                "Validation failed",
                                errors,
                                "One or more fields have validation errors",
                                HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatch(
                        MethodArgumentTypeMismatchException ex,
                        WebRequest request) {
                log.error("Type mismatch: {}", ex.getMessage());
                Class<?> requiredType = ex.getRequiredType();
                String typeName = requiredType != null ? requiredType.getSimpleName() : "unknown";
                String error = String.format("The parameter '%s' should be of type %s", ex.getName(), typeName);
                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                "Invalid parameter type",
                                null,
                                error,
                                HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException ex,
                        WebRequest request) {
                log.error("Authentication error: {}", ex.getMessage());
                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                "Authentication failed",
                                null,
                                ex.getMessage(),
                                HttpStatus.UNAUTHORIZED.value());
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException ex,
                        WebRequest request) {
                log.error("Bad credentials: {}", ex.getMessage());
                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                "Invalid credentials",
                                null,
                                "The provided credentials are invalid",
                                HttpStatus.UNAUTHORIZED.value());
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex,
                        WebRequest request) {
                log.error("Access denied: {}", ex.getMessage());
                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                "Access denied",
                                null,
                                "You don't have permission to access this resource",
                                HttpStatus.FORBIDDEN.value());
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex,
                        WebRequest request) {
                log.error("Illegal argument: {}", ex.getMessage());
                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                "Invalid argument",
                                null,
                                ex.getMessage(),
                                HttpStatus.BAD_REQUEST.value());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex, WebRequest request) {
                log.error("Unexpected error occurred: ", ex);
                ApiResponse<Object> response = new ApiResponse<>(
                                false,
                                "Internal server error",
                                null,
                                "An unexpected error occurred. Please try again later.",
                                HttpStatus.INTERNAL_SERVER_ERROR.value());
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}

# Global Error Handling Documentation

## Overview
The API Gateway now includes comprehensive global error handling with consistent JSON responses for all errors.

## Files Created

### Exception Classes
- `NotFoundException.java` - For 404 errors
- `BadRequestException.java` - For 400 errors
- `UnauthorizedException.java` - For 401 errors
- `ForbiddenException.java` - For 403 errors
- `ServiceUnavailableException.java` - For 503 errors

### Handler
- `GlobalExceptionHandler.java` - Centralized exception handler with @ControllerAdvice

### Test Controller
- `ErrorTestController.java` - Test endpoints to verify error handling

## Error Response Format

All errors return a consistent JSON response using the `ApiResponse` format:

```json
{
  "success": false,
  "message": "Error type description",
  "data": null,
  "error": "Detailed error message",
  "status": 404
}
```

## Handled Exception Types

1. **NotFoundException (404)**
   - Custom exception for resource not found scenarios

2. **BadRequestException (400)**
   - Custom exception for invalid requests

3. **UnauthorizedException (401)**
   - Custom exception for authentication failures

4. **ForbiddenException (403)**
   - Custom exception for authorization failures

5. **ServiceUnavailableException (503)**
   - Custom exception for service unavailability

6. **NoHandlerFoundException (404)**
   - Spring exception for undefined endpoints

7. **NoResourceFoundException (404)**
   - Spring exception for missing resources

8. **MethodArgumentNotValidException (400)**
   - Validation errors with field-level details

9. **MethodArgumentTypeMismatchException (400)**
   - Type conversion errors

10. **AuthenticationException (401)**
    - Spring Security authentication failures

11. **BadCredentialsException (401)**
    - Invalid credentials

12. **AccessDeniedException (403)**
    - Spring Security authorization failures

13. **IllegalArgumentException (400)**
    - Invalid method arguments

14. **Exception (500)**
    - Catch-all for unexpected errors

## Configuration

The following settings in `application.yaml` enable proper error handling:

```yaml
server:
  error:
    include-message: always
    include-binding-errors: always
    include-stacktrace: on_param
    include-exception: false

spring:
  mvc:
    throw-exception-if-no-handler-found: true
  web:
    resources:
      add-mappings: false
```

## Testing Endpoints

Use these test endpoints to verify error handling (available at `/test-errors/**`):

- `GET /test-errors/not-found` - Test 404 error
- `GET /test-errors/bad-request` - Test 400 error
- `GET /test-errors/unauthorized` - Test 401 error
- `GET /test-errors/forbidden` - Test 403 error
- `GET /test-errors/service-unavailable` - Test 503 error
- `GET /test-errors/internal-error` - Test 500 error
- `GET /test-errors/illegal-argument` - Test 400 error

**Note:** Remove the `ErrorTestController.java` file and the `/test-errors/**` permit rule from `SecurityConfig.java` before deploying to production.

## Usage Examples

### In Controllers

```java
// Throw a 404 error
if (user == null) {
    throw new NotFoundException("User not found");
}

// Throw a 400 error
if (request.getEmail() == null) {
    throw new BadRequestException("Email is required");
}

// Throw a 401 error
if (!isAuthenticated) {
    throw new UnauthorizedException("Invalid authentication token");
}

// Throw a 403 error
if (!hasPermission) {
    throw new ForbiddenException("You don't have permission to access this resource");
}

// Throw a 503 error
if (!serviceAvailable) {
    throw new ServiceUnavailableException("The service is temporarily unavailable");
}
```

### Undefined Routes

Any request to an undefined endpoint will automatically return:

```json
{
  "success": false,
  "message": "Endpoint not found",
  "data": null,
  "error": "No endpoint found for GET /undefined-path",
  "status": 404
}
```

## Benefits

1. **Consistent API responses** - All errors follow the same format
2. **Proper HTTP status codes** - Clients receive appropriate status codes
3. **Detailed logging** - All exceptions are logged with context
4. **Developer-friendly** - Clear error messages for debugging
5. **Production-ready** - Sensitive details hidden (no stack traces by default)
6. **Type-safe** - Custom exceptions for different scenarios

## Production Considerations

Before deploying to production:

1. Remove or restrict access to the `/test-errors/**` endpoints
2. Consider adding more specific exception types as needed
3. Review log levels to avoid logging sensitive information
4. Consider adding error monitoring/alerting integration
5. Add rate limiting to error endpoints to prevent abuse

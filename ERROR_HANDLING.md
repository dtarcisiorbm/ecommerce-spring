# Error Handling Documentation

## Overview

This document describes the comprehensive error handling system implemented in the E-commerce Backend application. The system provides consistent error responses, security logging, and robust exception handling across all layers.

## Architecture

### Exception Handlers

#### GlobalExceptionHandler
Located at `src/main/java/com/ecommerce_backend/backend/entrypoints/exceptions/GlobalExceptionHandler.java`

**Handles:**
- `IllegalArgumentException` → 400 Bad Request
- `IllegalStateException` → 409 Conflict
- `EmptyResultDataAccessException` → 404 Not Found
- `DataIntegrityViolationException` → 409 Conflict
- `RuntimeException` → 500 Internal Server Error
- `MethodArgumentNotValidException` → 400 Bad Request

#### SecurityExceptionHandler
Located at `src/main/java/com/ecommerce_backend/backend/entrypoints/exceptions/SecurityExceptionHandler.java`

**Handles:**
- `BadCredentialsException` → 401 Unauthorized (with security logging)
- `AccessDeniedException` → 403 Forbidden (with audit logging)

## Error Response Format

All errors return a standardized JSON response:

```json
{
  "status": 400,
  "message": "Validation failed: name: must not be blank",
  "timestamp": "2026-04-01T12:00:00",
  "path": "/api/products"
}
```

### Fields
- `status`: HTTP status code
- `message`: Human-readable error description
- `timestamp`: When the error occurred (ISO 8601 format)
- `path`: The request path that generated the error

## Layer-Specific Error Handling

### Repository Layer

#### Safety Checks
Before performing delete operations, repositories validate entity existence:

```java
@Override
public void deleteById(UUID id) {
    if (!repository.existsById(id)) {
        throw new IllegalArgumentException("Customer not found with id: " + id);
    }
    repository.deleteById(id);
}
```

#### Benefits
- Prevents `EmptyResultDataAccessException`
- Provides clear, business-friendly error messages
- Maintains consistency across delete operations

### Controller Layer

#### Validation Strategy
Controllers use `@Valid` annotations and proper exception handling:

```java
@PostMapping
public ResponseEntity<Product> createProduct(@RequestBody @Valid Product productRequest) {
    try {
        Product created = createProductUseCase.execute(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    } catch (IllegalArgumentException ex) {
        throw ex; // Deixa o GlobalExceptionHandler tratar
    } catch (RuntimeException ex) {
        throw new RuntimeException("Failed to create product", ex);
    }
}
```

#### Benefits
- Automatic validation via Bean Validation
- Consistent error response format
- Proper exception propagation

### Service Layer (Use Cases)

#### Business Exception Patterns
Use cases throw specific exceptions for business rule violations:

```java
public User execute(String email, String password) {
    User user = userGateway.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));

    if (!hasher.matches(password, user.password())) {
        throw new IllegalArgumentException("Credenciais inválidas.");
    }

    if (!user.active()) {
        throw new IllegalStateException("Esta conta está desativada.");
    }
    return user;
}
```

## Security Features

### Authentication Logging
Failed authentication attempts are logged with IP addresses:

```java
@ExceptionHandler(BadCredentialsException.class)
public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
    logger.warn("Authentication failed for IP: {}", request.getRemoteAddr());
    
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid credentials",
                LocalDateTime.now(),
                request.getRequestURI()
            ));
}
```

### Access Control Logging
Access denied events are audited:

```java
@ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
    logger.warn("Access denied for IP: {} on path: {}", request.getRemoteAddr(), request.getRequestURI());
    
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access denied",
                LocalDateTime.now(),
                request.getRequestURI()
            ));
}
```

## Error Categories

### Client Errors (4xx)

#### 400 Bad Request
- Validation failures
- Invalid input data
- Malformed requests

#### 401 Unauthorized
- Invalid authentication credentials
- Expired tokens

#### 403 Forbidden
- Insufficient permissions
- Access denied to protected resources

#### 404 Not Found
- Resource does not exist
- Invalid entity IDs

#### 409 Conflict
- Data integrity violations
- Duplicate resource creation
- Business rule conflicts

### Server Errors (5xx)

#### 500 Internal Server Error
- Unexpected runtime exceptions
- System failures
- External service errors

## Best Practices

### For Developers

1. **Use Specific Exceptions**: Choose the most appropriate exception type
2. **Provide Clear Messages**: Include context without exposing sensitive data
3. **Log Security Events**: Always log authentication and authorization failures
4. **Validate Input**: Use Bean Validation annotations
5. **Handle Business Rules**: Use `IllegalArgumentException` for business logic violations

### Error Message Guidelines

- **Be Specific**: "Product not found with id: 123" vs "Resource not found"
- **Be User-Friendly**: Avoid technical jargon in client-facing messages
- **Be Consistent**: Use similar message formats across the application
- **Be Secure**: Don't expose internal system details or stack traces

## Testing Error Handling

### Unit Tests
Test exception scenarios in use cases and services:

```java
@Test
void shouldThrowExceptionWhenCustomerNotFound() {
    when(repository.findById(any())).thenReturn(Optional.empty());
    
    assertThatThrownBy(() -> useCase.execute(UUID.randomUUID()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Customer not found");
}
```

### Integration Tests
Test complete error flows in controllers:

```java
@Test
void shouldReturn400WhenInvalidData() throws Exception {
    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.path").value("/products"));
}
```

## Monitoring and Alerting

### Log Levels
- **WARN**: Authentication failures, access denied
- **ERROR**: Unexpected exceptions, system failures
- **INFO**: Successful operations (optional)

### Metrics to Monitor
- Error rates by endpoint
- Authentication failure patterns
- Database constraint violations
- Response time for error scenarios

## Migration Guide

### Breaking Changes
The new error response format includes `timestamp` and `path` fields. Update client applications:

**Before:**
```json
{
  "status": 400,
  "message": "Invalid input"
}
```

**After:**
```json
{
  "status": 400,
  "message": "Invalid input",
  "timestamp": "2026-04-01T12:00:00",
  "path": "/api/products"
}
```

### Client Update Checklist
- [ ] Handle additional fields in error responses
- [ ] Update error parsing logic
- [ ] Test error scenarios
- [ ] Update error message display

## Troubleshooting

### Common Issues

1. **Empty Error Responses**: Check if exceptions are being caught and re-thrown properly
2. **Missing Timestamps**: Verify `HttpServletRequest` is injected in exception handlers
3. **Logging Not Working**: Ensure log level is configured correctly in `application.properties`
4. **Validation Not Triggered**: Check `@Valid` annotation usage and validation setup

### Debug Configuration
Add to `application.properties` for detailed error logging:

```properties
logging.level.com.ecommerce_backend.backend=WARN
logging.level.org.springframework.security=DEBUG
```

---

## Conclusion

This error handling system provides:
- ✅ Consistent error responses
- ✅ Security audit logging
- ✅ Comprehensive exception coverage
- ✅ Developer-friendly debugging information
- ✅ Production-ready error management

For questions or improvements, please contact the development team or create an issue in the project repository.

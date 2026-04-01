package com.ecommerce_backend.backend.entrypoints.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
public class SecurityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(SecurityExceptionHandler.class);

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        logger.warn("Authentication failed for IP: {} on path: {}", request.getRemoteAddr(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Authentication failed",
                    "Invalid or missing authentication credentials",
                    "Please provide valid authentication credentials to access this resource",
                    LocalDateTime.now(),
                    request.getRequestURI()
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        logger.warn("Invalid credentials for IP: {} on path: {}", request.getRemoteAddr(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid credentials",
                    "The provided username or password is incorrect",
                    "Please check your credentials and try again",
                    LocalDateTime.now(),
                    request.getRequestURI()
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        logger.warn("Access denied for IP: {} on path: {}", request.getRemoteAddr(), request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(
                    HttpStatus.FORBIDDEN.value(),
                    "Access denied",
                    "You don't have permission to access this resource",
                    "Contact your administrator if you believe this is an error",
                    LocalDateTime.now(),
                    request.getRequestURI()
                ));
    }

    public record ErrorResponse(
        int status, 
        String error, 
        String message, 
        String details,
        LocalDateTime timestamp, 
        String path
    ) {}
}

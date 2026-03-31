package com.ecommerce_backend.backend.entrypoints.dto;

public record TokenValidationResponse(
    boolean valid,
    String subject
) {}

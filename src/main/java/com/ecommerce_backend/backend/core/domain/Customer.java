package com.ecommerce_backend.backend.core.domain;
public record Customer(
        Long id,
        String fullName,
        String email,
        String taxId // CPF/CNPJ
) {}

package com.ecommerce_backend.backend.core.domain;

import java.util.UUID;

public record Customer(
        UUID id,
        String fullName,
        String email,
        String taxId,
        String password // Adicionado campo de senha
) {}
package com.ecommerce_backend.backend.entrypoints.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String taxId,
        @Size(min = 6) String password // Opcional para atualização
) {}
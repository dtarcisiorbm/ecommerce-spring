package com.ecommerce_backend.backend.entrypoints.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String taxId,
        @NotBlank @Size(min = 6) String password // Adicionado para o registo
) {}
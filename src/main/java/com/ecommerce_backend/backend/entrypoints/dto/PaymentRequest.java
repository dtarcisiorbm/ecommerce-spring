package com.ecommerce_backend.backend.entrypoints.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
        @NotNull UUID orderId,
        
        @NotBlank(message = "Payment method is required")
        String method,
        
        @NotBlank(message = "Card number is required")
        @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
        String cardNumber,
        
        @NotBlank(message = "Card holder name is required")
        String cardHolderName,
        
        @NotBlank(message = "Expiry date is required")
        @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Expiry date must be in MM/YY format")
        String expiryDate,
        
        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "\\d{3,4}", message = "CVV must be 3 or 4 digits")
        String cvv,
        
        @NotBlank(message = "Email is required")
        @jakarta.validation.constraints.Email(message = "Invalid email format")
        String email,
        
        String billingAddress,
        
        String postalCode
) {}

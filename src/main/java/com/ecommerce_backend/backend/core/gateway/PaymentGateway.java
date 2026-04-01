package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.Payment;
import com.ecommerce_backend.backend.core.domain.PaymentMethod;
import com.ecommerce_backend.backend.core.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentGateway {
    
    // Processamento de Pagamentos
    Payment processPayment(UUID orderId, BigDecimal amount, PaymentMethod method, PaymentRequest paymentRequest);
    Optional<Payment> findByOrderId(UUID orderId);
    Optional<Payment> findById(UUID id);
    Payment save(Payment payment);
    
    // Refunds
    Payment refundPayment(UUID paymentId, BigDecimal amount);
    Payment refundFullPayment(UUID paymentId);
    
    // Webhooks
    void handleWebhook(String gateway, String payload);
    
    // Consultas
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByDateRange(LocalDateTime start, LocalDateTime end);
    
    record PaymentRequest(
        String cardNumber,
        String cardHolderName,
        String expiryDate,
        String cvv,
        String email,
        String billingAddress,
        String postalCode
    ) {}
}

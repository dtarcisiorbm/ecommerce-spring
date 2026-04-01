package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.Payment;
import com.ecommerce_backend.backend.core.domain.PaymentMethod;
import com.ecommerce_backend.backend.core.useCases.payment.ProcessPaymentUseCase;
import com.ecommerce_backend.backend.core.useCases.payment.RefundPaymentUseCase;
import com.ecommerce_backend.backend.core.gateway.PaymentGateway;
import com.ecommerce_backend.backend.entrypoints.dto.PaymentRequest;
import com.ecommerce_backend.backend.entrypoints.dto.RefundRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final RefundPaymentUseCase refundPaymentUseCase;
    private final PaymentGateway paymentGateway;

    public PaymentController(
            ProcessPaymentUseCase processPaymentUseCase,
            RefundPaymentUseCase refundPaymentUseCase,
            PaymentGateway paymentGateway) {
        this.processPaymentUseCase = processPaymentUseCase;
        this.refundPaymentUseCase = refundPaymentUseCase;
        this.paymentGateway = paymentGateway;
    }

    /**
     * Processar pagamento para um pedido
     */
    @PostMapping("/process")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Payment> processPayment(@RequestBody @Valid PaymentRequest request) {
        try {
            Payment payment = processPaymentUseCase.execute(
                    request.orderId(),
                    PaymentMethod.valueOf(request.method()),
                    new PaymentGateway.PaymentRequest(
                            request.cardNumber(),
                            request.cardHolderName(),
                            request.expiryDate(),
                            request.cvv(),
                            request.email(),
                            request.billingAddress(),
                            request.postalCode()
                    )
            );
            return ResponseEntity.status(201).body(payment);
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        }
    }

    /**
     * Buscar pagamento por ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<Payment> getPayment(@PathVariable UUID id) {
        return paymentGateway.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Buscar pagamento por pedido
     */
    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
    public ResponseEntity<Payment> getPaymentByOrder(@PathVariable UUID orderId) {
        return paymentGateway.findByOrderId(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Listar pagamentos por status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Payment>> getPaymentsByStatus(@PathVariable String status) {
        try {
            com.ecommerce_backend.backend.core.domain.PaymentStatus paymentStatus = 
                    com.ecommerce_backend.backend.core.domain.PaymentStatus.valueOf(status.toUpperCase());
            List<Payment> payments = paymentGateway.findByStatus(paymentStatus);
            return ResponseEntity.ok(payments);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid payment status: " + status);
        }
    }

    /**
     * Refund parcial de pagamento
     */
    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Payment> refundPayment(
            @PathVariable UUID paymentId,
            @RequestBody @Valid RefundRequest request) {
        try {
            Payment payment = refundPaymentUseCase.execute(paymentId, request.amount());
            return ResponseEntity.ok(payment);
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        }
    }

    /**
     * Refund completo de pagamento
     */
    @PostMapping("/{paymentId}/refund/full")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Payment> refundFullPayment(@PathVariable UUID paymentId) {
        try {
            Payment payment = refundPaymentUseCase.executeFullRefund(paymentId);
            return ResponseEntity.ok(payment);
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        }
    }

    /**
     * Webhook para gateways de pagamento
     */
    @PostMapping("/webhook/{gateway}")
    public ResponseEntity<Void> handleWebhook(
            @PathVariable String gateway,
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {
        try {
            // Em um caso real, validar a assinatura do webhook
            paymentGateway.handleWebhook(gateway, payload);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            throw new RuntimeException("Webhook processing failed", ex);
        }
    }
}

package com.ecommerce_backend.backend.infrastructure.payment;

import com.ecommerce_backend.backend.core.domain.Payment;
import com.ecommerce_backend.backend.core.domain.PaymentMethod;
import com.ecommerce_backend.backend.core.domain.PaymentStatus;
import com.ecommerce_backend.backend.core.gateway.PaymentGateway;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.PaymentEntity;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.PaymentRepository;
import com.ecommerce_backend.backend.entrypoints.mapper.PaymentMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Refund;
import com.stripe.param.ChargeCreateParams;
import com.stripe.param.RefundCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class StripePaymentGateway implements PaymentGateway {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public StripePaymentGateway(
            PaymentRepository paymentRepository,
            PaymentMapper paymentMapper,
            @Value("${stripe.secret.key}") String stripeSecretKey) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public Payment processPayment(UUID orderId, BigDecimal amount, PaymentMethod method, PaymentRequest paymentRequest) {
        try {
            // Criar pagamento no Stripe
            ChargeCreateParams params = ChargeCreateParams.builder()
                    .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue()) // Stripe usa centavos
                    .setCurrency("brl")
                    .setDescription("Payment for order: " + orderId)
                    .setSource(paymentRequest.cardNumber()) // Em produção, usar token do Stripe
                    .setMetadata(Map.of(
                            "orderId", orderId.toString(),
                            "email", paymentRequest.email()
                    ))
                    .build();

            Charge charge = Charge.create(params);

            // Criar entidade de pagamento
            PaymentEntity paymentEntity = PaymentEntity.builder()
                    .id(UUID.randomUUID())
                    .orderId(orderId)
                    .amount(amount)
                    .method(method)
                    .status(charge.getPaid() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED)
                    .transactionId(charge.getId())
                    .gatewayResponse(charge.toJson())
                    .processedAt(charge.getPaid() ? 
                            LocalDateTime.ofInstant(java.time.Instant.ofEpochSecond(charge.getCreated()), ZoneId.systemDefault()) : null)
                    .build();

            PaymentEntity saved = paymentRepository.save(paymentEntity);
            return paymentMapper.toDomain(saved);

        } catch (StripeException e) {
            // Criar pagamento com status FAILED
            PaymentEntity failedPayment = PaymentEntity.builder()
                    .id(UUID.randomUUID())
                    .orderId(orderId)
                    .amount(amount)
                    .method(method)
                    .status(PaymentStatus.FAILED)
                    .gatewayResponse("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();

            PaymentEntity saved = paymentRepository.save(failedPayment);
            return paymentMapper.toDomain(saved);
        }
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(paymentMapper::toDomain);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return paymentRepository.findById(id)
                .map(paymentMapper::toDomain);
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = paymentMapper.toEntity(payment);
        PaymentEntity saved = paymentRepository.save(entity);
        return paymentMapper.toDomain(saved);
    }

    @Override
    public Payment refundPayment(UUID paymentId, BigDecimal amount) {
        try {
            Optional<PaymentEntity> paymentOpt = paymentRepository.findById(paymentId);
            if (paymentOpt.isEmpty()) {
                throw new IllegalArgumentException("Payment not found: " + paymentId);
            }

            PaymentEntity payment = paymentOpt.get();

            // Criar refund no Stripe
            RefundCreateParams params = RefundCreateParams.builder()
                    .setCharge(payment.getTransactionId())
                    .setAmount(amount.multiply(BigDecimal.valueOf(100)).longValue())
                    .build();

            Refund refund = Refund.create(params);

            // Atualizar status do pagamento
            PaymentStatus newStatus = amount.equals(payment.getAmount()) ? 
                    PaymentStatus.REFUNDED : PaymentStatus.PARTIALLY_REFUNDED;

            payment.setStatus(newStatus);
            payment.setGatewayResponse(payment.getGatewayResponse() + "\n" + refund.toJson());

            PaymentEntity saved = paymentRepository.save(payment);
            return paymentMapper.toDomain(saved);

        } catch (StripeException e) {
            throw new RuntimeException("Refund failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Payment refundFullPayment(UUID paymentId) {
        Optional<Payment> paymentOpt = findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        }

        return refundPayment(paymentId, paymentOpt.get().amount());
    }

    @Override
    public void handleWebhook(String gateway, String payload) {
        // Implementar webhook do Stripe para eventos como payment_succeeded, payment_failed, etc.
        // Em um caso real, validar assinatura do webhook
        System.out.println("Webhook received from " + gateway + ": " + payload);
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status)
                .stream()
                .map(paymentMapper::toDomain)
                .toList();
    }

    @Override
    public List<Payment> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByDateRange(start, end)
                .stream()
                .map(paymentMapper::toDomain)
                .toList();
    }
}

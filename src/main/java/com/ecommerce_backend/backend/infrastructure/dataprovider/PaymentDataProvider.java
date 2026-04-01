package com.ecommerce_backend.backend.infrastructure.dataprovider;

import com.ecommerce_backend.backend.core.domain.Payment;
import com.ecommerce_backend.backend.core.domain.PaymentMethod;
import com.ecommerce_backend.backend.core.gateway.PaymentGateway;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.PaymentEntity;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.PaymentRepository;
import com.ecommerce_backend.backend.entrypoints.mapper.PaymentMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PaymentDataProvider implements PaymentGateway {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentDataProvider(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public Payment processPayment(UUID orderId, BigDecimal amount, PaymentMethod method, PaymentRequest paymentRequest) {
        // Implementação padrão - delegar para gateway específico
        PaymentEntity paymentEntity = PaymentEntity.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .amount(amount)
                .method(method)
                .status(com.ecommerce_backend.backend.core.domain.PaymentStatus.PENDING)
                .gatewayResponse("{\"message\": \"Payment processed via default gateway\"}")
                .build();

        PaymentEntity saved = paymentRepository.save(paymentEntity);
        return paymentMapper.toDomain(saved);
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
    @Transactional
    public Payment save(Payment payment) {
        PaymentEntity entity = paymentMapper.toEntity(payment);
        PaymentEntity saved = paymentRepository.save(entity);
        return paymentMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public Payment refundPayment(UUID paymentId, BigDecimal amount) {
        Optional<PaymentEntity> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        }

        PaymentEntity payment = paymentOpt.get();
        
        // Atualizar status para refund
        com.ecommerce_backend.backend.core.domain.PaymentStatus newStatus = amount.equals(payment.getAmount()) ? 
                com.ecommerce_backend.backend.core.domain.PaymentStatus.REFUNDED : 
                com.ecommerce_backend.backend.core.domain.PaymentStatus.PARTIALLY_REFUNDED;

        payment.setStatus(newStatus);
        payment.setGatewayResponse(payment.getGatewayResponse() + "\n{\"refunded\": " + amount + "}");

        PaymentEntity saved = paymentRepository.save(payment);
        return paymentMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public Payment refundFullPayment(UUID paymentId) {
        Optional<Payment> paymentOpt = findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        }

        return refundPayment(paymentId, paymentOpt.get().amount());
    }

    @Override
    public void handleWebhook(String gateway, String payload) {
        // Implementação padrão - log do webhook
        System.out.println("Webhook received from " + gateway + ": " + payload);
    }

    @Override
    public List<Payment> findByStatus(com.ecommerce_backend.backend.core.domain.PaymentStatus status) {
        return paymentRepository.findByStatus(status)
                .stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return paymentRepository.findByDateRange(start, end)
                .stream()
                .map(paymentMapper::toDomain)
                .collect(Collectors.toList());
    }
}

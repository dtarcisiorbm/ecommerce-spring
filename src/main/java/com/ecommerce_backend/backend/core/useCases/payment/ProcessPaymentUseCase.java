package com.ecommerce_backend.backend.core.useCases.payment;

import com.ecommerce_backend.backend.core.domain.Order;
import com.ecommerce_backend.backend.core.domain.Payment;
import com.ecommerce_backend.backend.core.domain.PaymentMethod;
import com.ecommerce_backend.backend.core.gateway.OrderGateway;
import com.ecommerce_backend.backend.core.gateway.PaymentGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProcessPaymentUseCase {
    private final PaymentGateway paymentGateway;
    private final OrderGateway orderGateway;

    public ProcessPaymentUseCase(PaymentGateway paymentGateway, OrderGateway orderGateway) {
        this.paymentGateway = paymentGateway;
        this.orderGateway = orderGateway;
    }

    @Transactional
    public Payment execute(UUID orderId, PaymentMethod method, PaymentGateway.PaymentRequest paymentRequest) {
        // Validar ordem
        Optional<Order> orderOpt = orderGateway.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        Order order = orderOpt.get();

        // Validar status da ordem
        if (order.status() != com.ecommerce_backend.backend.core.domain.OrderStatus.PENDING) {
            throw new IllegalArgumentException("Order is not pending payment. Current status: " + order.status());
        }

        // Calcular valor total
        BigDecimal totalAmount = calculateTotalAmount(order);

        // Processar pagamento
        Payment payment = paymentGateway.processPayment(orderId, totalAmount, method, paymentRequest);

        // Atualizar status da ordem se pagamento for bem-sucedido
        if (payment.status() == com.ecommerce_backend.backend.core.domain.PaymentStatus.COMPLETED) {
            orderGateway.updateStatus(orderId, com.ecommerce_backend.backend.core.domain.OrderStatus.PAID);
        }

        return payment;
    }

    private BigDecimal calculateTotalAmount(Order order) {
        return order.items().stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

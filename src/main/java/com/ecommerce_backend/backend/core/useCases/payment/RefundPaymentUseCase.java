package com.ecommerce_backend.backend.core.useCases.payment;

import com.ecommerce_backend.backend.core.domain.Payment;
import com.ecommerce_backend.backend.core.gateway.PaymentGateway;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefundPaymentUseCase {
    private final PaymentGateway paymentGateway;

    public RefundPaymentUseCase(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public Payment execute(UUID paymentId, BigDecimal refundAmount) {
        // Validar pagamento
        Optional<Payment> paymentOpt = paymentGateway.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        }

        Payment payment = paymentOpt.get();

        // Validar status do pagamento
        if (payment.status() != com.ecommerce_backend.backend.core.domain.PaymentStatus.COMPLETED) {
            throw new IllegalArgumentException("Payment cannot be refunded. Current status: " + payment.status());
        }

        // Validar valor do refund
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refund amount must be positive");
        }

        if (refundAmount.compareTo(payment.amount()) > 0) {
            throw new IllegalArgumentException("Refund amount cannot exceed payment amount");
        }

        return paymentGateway.refundPayment(paymentId, refundAmount);
    }

    public Payment executeFullRefund(UUID paymentId) {
        return paymentGateway.refundFullPayment(paymentId);
    }
}

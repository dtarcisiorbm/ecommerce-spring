package com.ecommerce_backend.backend.infrastructure.config;

import com.ecommerce_backend.backend.core.gateway.OrderGateway;
import com.ecommerce_backend.backend.core.gateway.PaymentGateway;
import com.ecommerce_backend.backend.core.useCases.payment.ProcessPaymentUseCase;
import com.ecommerce_backend.backend.core.useCases.payment.RefundPaymentUseCase;
import com.ecommerce_backend.backend.infrastructure.payment.StripePaymentGateway;
import com.ecommerce_backend.backend.infrastructure.dataprovider.PaymentDataProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentConfig {

    @Bean
    public ProcessPaymentUseCase processPaymentUseCase(PaymentGateway paymentGateway, OrderGateway orderGateway) {
        return new ProcessPaymentUseCase(paymentGateway, orderGateway);
    }

    @Bean
    public RefundPaymentUseCase refundPaymentUseCase(PaymentGateway paymentGateway) {
        return new RefundPaymentUseCase(paymentGateway);
    }

    @Bean
    @ConditionalOnProperty(name = "payment.gateway", havingValue = "stripe")
    public PaymentGateway stripePaymentGateway(
            com.ecommerce_backend.backend.infrastructure.dataprovider.repository.PaymentRepository paymentRepository,
            com.ecommerce_backend.backend.entrypoints.mapper.PaymentMapper paymentMapper,
            @Value("${stripe.secret.key:}") String stripeSecretKey) {
        return new StripePaymentGateway(paymentRepository, paymentMapper, stripeSecretKey);
    }

    @Bean
    @ConditionalOnProperty(name = "payment.gateway", havingValue = "default", matchIfMissing = true)
    @ConditionalOnMissingBean(name = "stripePaymentGateway")
    public PaymentGateway defaultPaymentGateway(
            com.ecommerce_backend.backend.infrastructure.dataprovider.repository.PaymentRepository paymentRepository,
            com.ecommerce_backend.backend.entrypoints.mapper.PaymentMapper paymentMapper) {
        return new PaymentDataProvider(paymentRepository, paymentMapper);
    }
}

package com.ecommerce_backend.backend.core.domain;

public enum OrderStatus {
    PENDING,    // Aguardando pagamento
    PAID,       // Pagamento confirmado
    SHIPPED,    // Enviado
    DELIVERED,  // Entregue
    CANCELED    // Cancelado
}
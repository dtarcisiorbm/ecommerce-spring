package com.ecommerce_backend.backend.core.useCases.cart;

import com.ecommerce_backend.backend.core.gateway.ShoppingCartGateway;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClearCartUseCase {
    private final ShoppingCartGateway shoppingCartGateway;

    public ClearCartUseCase(ShoppingCartGateway shoppingCartGateway) {
        this.shoppingCartGateway = shoppingCartGateway;
    }

    public void execute(UUID customerId) {
        shoppingCartGateway.clearCart(customerId);
    }
}

package com.ecommerce_backend.backend.core.useCases.cart;

import com.ecommerce_backend.backend.core.gateway.ShoppingCartGateway;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RemoveItemFromCartUseCase {
    private final ShoppingCartGateway shoppingCartGateway;

    public RemoveItemFromCartUseCase(ShoppingCartGateway shoppingCartGateway) {
        this.shoppingCartGateway = shoppingCartGateway;
    }

    public void execute(UUID itemId, UUID customerId) {
        // Validação básica - em um caso real, verificaríamos se o item pertence ao carrinho do cliente
        shoppingCartGateway.deleteItem(itemId);
    }
}

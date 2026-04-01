package com.ecommerce_backend.backend.core.useCases.cart;

import com.ecommerce_backend.backend.core.domain.ShoppingCart;
import com.ecommerce_backend.backend.core.gateway.ShoppingCartGateway;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetOrCreateCartUseCase {
    private final ShoppingCartGateway shoppingCartGateway;

    public GetOrCreateCartUseCase(ShoppingCartGateway shoppingCartGateway) {
        this.shoppingCartGateway = shoppingCartGateway;
    }

    public ShoppingCart execute(UUID customerId) {
        Optional<ShoppingCart> existingCart = shoppingCartGateway.findByCustomerId(customerId);
        
        if (existingCart.isPresent()) {
            return existingCart.get();
        }
        
        // Cria novo carrinho para o cliente
        ShoppingCart newCart = new ShoppingCart(null, customerId, null, null);
        return shoppingCartGateway.save(newCart);
    }
}

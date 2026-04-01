package com.ecommerce_backend.backend.core.useCases.cart;

import com.ecommerce_backend.backend.core.domain.ShoppingCart;
import com.ecommerce_backend.backend.core.domain.ShoppingCartItem;
import com.ecommerce_backend.backend.core.gateway.ShoppingCartGateway;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetCartUseCase {
    private final ShoppingCartGateway shoppingCartGateway;

    public GetCartUseCase(ShoppingCartGateway shoppingCartGateway) {
        this.shoppingCartGateway = shoppingCartGateway;
    }

    public CartResponse execute(UUID customerId) {
        ShoppingCart cart = shoppingCartGateway.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for customer: " + customerId));

        List<ShoppingCartItem> items = shoppingCartGateway.findByShoppingCartId(cart.id());

        return new CartResponse(cart, items);
    }

    public record CartResponse(ShoppingCart cart, List<ShoppingCartItem> items) {}
}

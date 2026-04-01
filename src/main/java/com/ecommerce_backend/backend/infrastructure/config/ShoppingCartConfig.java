package com.ecommerce_backend.backend.infrastructure.config;

import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import com.ecommerce_backend.backend.core.gateway.ShoppingCartGateway;
import com.ecommerce_backend.backend.core.useCases.cart.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShoppingCartConfig {

    @Bean
    public GetOrCreateCartUseCase getOrCreateCartUseCase(ShoppingCartGateway shoppingCartGateway) {
        return new GetOrCreateCartUseCase(shoppingCartGateway);
    }

    @Bean
    public GetCartUseCase getCartUseCase(ShoppingCartGateway shoppingCartGateway) {
        return new GetCartUseCase(shoppingCartGateway);
    }

    @Bean
    public AddItemToCartUseCase addItemToCartUseCase(ShoppingCartGateway shoppingCartGateway, ProductGateway productGateway) {
        return new AddItemToCartUseCase(shoppingCartGateway, productGateway);
    }

    @Bean
    public UpdateCartItemUseCase updateCartItemUseCase(ShoppingCartGateway shoppingCartGateway, ProductGateway productGateway) {
        return new UpdateCartItemUseCase(shoppingCartGateway, productGateway);
    }

    @Bean
    public RemoveItemFromCartUseCase removeItemFromCartUseCase(ShoppingCartGateway shoppingCartGateway) {
        return new RemoveItemFromCartUseCase(shoppingCartGateway);
    }

    @Bean
    public ClearCartUseCase clearCartUseCase(ShoppingCartGateway shoppingCartGateway) {
        return new ClearCartUseCase(shoppingCartGateway);
    }
}

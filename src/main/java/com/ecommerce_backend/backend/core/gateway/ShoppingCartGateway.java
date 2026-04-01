package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.ShoppingCart;
import com.ecommerce_backend.backend.core.domain.ShoppingCartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartGateway {
    // Carrinho
    ShoppingCart save(ShoppingCart shoppingCart);
    Optional<ShoppingCart> findByCustomerId(UUID customerId);
    Optional<ShoppingCart> findById(UUID id);
    void deleteById(UUID id);
    void clearCart(UUID customerId);
    
    // Itens do Carrinho
    ShoppingCartItem addItem(ShoppingCartItem item);
    ShoppingCartItem updateItem(ShoppingCartItem item);
    void removeItem(UUID itemId);
    List<ShoppingCartItem> findByShoppingCartId(UUID shoppingCartId);
    Optional<ShoppingCartItem> findByShoppingCartIdAndProductId(UUID shoppingCartId, UUID productId);
    void deleteItem(UUID itemId);
    Page<ShoppingCartItem> findAllItems(Pageable pageable);
}

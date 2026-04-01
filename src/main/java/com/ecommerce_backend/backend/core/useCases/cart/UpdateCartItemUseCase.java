package com.ecommerce_backend.backend.core.useCases.cart;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.domain.ShoppingCartItem;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import com.ecommerce_backend.backend.core.gateway.ShoppingCartGateway;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class UpdateCartItemUseCase {
    private final ShoppingCartGateway shoppingCartGateway;
    private final ProductGateway productGateway;

    public UpdateCartItemUseCase(ShoppingCartGateway shoppingCartGateway, ProductGateway productGateway) {
        this.shoppingCartGateway = shoppingCartGateway;
        this.productGateway = productGateway;
    }

    public ShoppingCartItem execute(UUID itemId, UUID customerId, Integer newQuantity) {
        // Validações
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // Busca o item atual
        Optional<ShoppingCartItem> itemOpt = shoppingCartGateway.findByShoppingCartIdAndProductId(
                getOrCreateCart(customerId).id(), 
                getItemProductId(itemId)
        );

        if (itemOpt.isEmpty()) {
            throw new IllegalArgumentException("Cart item not found with id: " + itemId);
        }

        ShoppingCartItem currentItem = itemOpt.get();
        
        // Busca o produto para verificar estoque
        Optional<Product> productOpt = productGateway.findById(currentItem.productId());
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found with id: " + currentItem.productId());
        }

        Product product = productOpt.get();

        // Verifica se há estoque suficiente
        if (!product.hasStock(newQuantity)) {
            throw new IllegalArgumentException(
                "Insufficient stock. Available: " + product.stock() + ", Requested: " + newQuantity
            );
        }

        // Atualiza o item
        ShoppingCartItem updatedItem = new ShoppingCartItem(
                itemId,
                currentItem.shoppingCartId(),
                currentItem.productId(),
                product.name(),
                product.sku(),
                product.price(),
                newQuantity,
                product.price().multiply(BigDecimal.valueOf(newQuantity))
        );

        return shoppingCartGateway.updateItem(updatedItem);
    }

    private ShoppingCart getOrCreateCart(UUID customerId) {
        Optional<ShoppingCart> existingCart = shoppingCartGateway.findByCustomerId(customerId);
        return existingCart.orElseGet(() -> shoppingCartGateway.save(
                new ShoppingCart(null, customerId, null, null))
        );
    }

    private UUID getItemProductId(UUID itemId) {
        // Implementação simplificada - em um caso real, buscaríamos o item pelo ID
        // Para este exemplo, vamos assumir que precisamos buscar o item
        throw new UnsupportedOperationException("Method to get product ID from item ID needs implementation");
    }
}

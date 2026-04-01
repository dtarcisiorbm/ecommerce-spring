package com.ecommerce_backend.backend.core.useCases.cart;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.core.domain.ShoppingCart;
import com.ecommerce_backend.backend.core.domain.ShoppingCartItem;
import com.ecommerce_backend.backend.core.gateway.ProductGateway;
import com.ecommerce_backend.backend.core.gateway.ShoppingCartGateway;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
public class AddItemToCartUseCase {
    private final ShoppingCartGateway shoppingCartGateway;
    private final ProductGateway productGateway;

    public AddItemToCartUseCase(ShoppingCartGateway shoppingCartGateway, ProductGateway productGateway) {
        this.shoppingCartGateway = shoppingCartGateway;
        this.productGateway = productGateway;
    }

    public ShoppingCartItem execute(UUID customerId, UUID productId, Integer quantity) {
        // Validações
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // Busca o produto
        Optional<Product> productOpt = productGateway.findById(productId);
        if (productOpt.isEmpty()) {
            throw new IllegalArgumentException("Product not found with id: " + productId);
        }

        Product product = productOpt.get();

        // Verifica se há estoque suficiente
        if (!product.hasStock(quantity)) {
            throw new IllegalArgumentException(
                "Insufficient stock. Available: " + product.stock() + ", Requested: " + quantity
            );
        }

        // Obtém ou cria o carrinho
        ShoppingCart cart = getOrCreateCart(customerId);

        // Verifica se o item já existe no carrinho
        Optional<ShoppingCartItem> existingItem = shoppingCartGateway
                .findByShoppingCartIdAndProductId(cart.id(), productId);

        if (existingItem.isPresent()) {
            // Atualiza quantidade do item existente
            ShoppingCartItem current = existingItem.get();
            int newQuantity = current.quantity() + quantity;
            
            // Verifica estoque novamente
            if (!product.hasStock(newQuantity)) {
                throw new IllegalArgumentException(
                    "Insufficient stock for updated quantity. Available: " + product.stock() + ", Requested: " + newQuantity
                );
            }

            ShoppingCartItem updatedItem = new ShoppingCartItem(
                    current.id(),
                    cart.id(),
                    productId,
                    product.name(),
                    product.sku(),
                    product.price(),
                    newQuantity,
                    product.price().multiply(BigDecimal.valueOf(newQuantity))
            );

            return shoppingCartGateway.updateItem(updatedItem);
        } else {
            // Adiciona novo item
            ShoppingCartItem newItem = new ShoppingCartItem(
                    null,
                    cart.id(),
                    productId,
                    product.name(),
                    product.sku(),
                    product.price(),
                    quantity,
                    product.price().multiply(BigDecimal.valueOf(quantity))
            );

            return shoppingCartGateway.addItem(newItem);
        }
    }

    private ShoppingCart getOrCreateCart(UUID customerId) {
        Optional<ShoppingCart> existingCart = shoppingCartGateway.findByCustomerId(customerId);
        return existingCart.orElseGet(() -> shoppingCartGateway.save(
                new ShoppingCart(null, customerId, null, null))
        );
    }
}

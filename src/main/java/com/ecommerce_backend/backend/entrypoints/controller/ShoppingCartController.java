package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.ShoppingCartItem;
import com.ecommerce_backend.backend.core.useCases.cart.*;
import com.ecommerce_backend.backend.entrypoints.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class ShoppingCartController {

    private final GetCartUseCase getCartUseCase;
    private final AddItemToCartUseCase addItemToCartUseCase;
    private final UpdateCartItemUseCase updateCartItemUseCase;
    private final RemoveItemFromCartUseCase removeItemFromCartUseCase;
    private final ClearCartUseCase clearCartUseCase;

    public ShoppingCartController(
            GetCartUseCase getCartUseCase,
            AddItemToCartUseCase addItemToCartUseCase,
            UpdateCartItemUseCase updateCartItemUseCase,
            RemoveItemFromCartUseCase removeItemFromCartUseCase,
            ClearCartUseCase clearCartUseCase) {
        this.getCartUseCase = getCartUseCase;
        this.addItemToCartUseCase = addItemToCartUseCase;
        this.updateCartItemUseCase = updateCartItemUseCase;
        this.removeItemFromCartUseCase = removeItemFromCartUseCase;
        this.clearCartUseCase = clearCartUseCase;
    }

    /**
     * Obter carrinho de compras do cliente
     */
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<CartResponse> getCart() {
        // Em um caso real, obteríamos o customerId do token JWT
        UUID customerId = UUID.fromString("550e8400-e29b-41d4-a716-446655440444"); // Exemplo
        
        try {
            var cartResponse = getCartUseCase.execute(customerId);
            return ResponseEntity.ok(cartResponse);
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        }
    }

    /**
     * Adicionar item ao carrinho
     */
    @PostMapping("/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ShoppingCartItem> addItem(@RequestBody @Valid AddCartItemRequest request) {
        // Em um caso real, obteríamos o customerId do token JWT
        UUID customerId = UUID.fromString("550e8400-e29b-41d4-a716-446655440444"); // Exemplo
        
        try {
            ShoppingCartItem item = addItemToCartUseCase.execute(
                    customerId, 
                    UUID.fromString(String.valueOf(request.productId())), 
                    request.quantity()
            );
            return ResponseEntity.status(201).body(item);
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        }
    }

    /**
     * Atualizar quantidade de item no carrinho
     */
    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ShoppingCartItem> updateItem(
            @PathVariable UUID itemId,
            @RequestBody @Valid UpdateCartItemRequest request) {
        // Em um caso real, obteríamos o customerId do token JWT
        UUID customerId = UUID.fromString("550e8400-e29b-41d4-a716-446655440444"); // Exemplo
        
        try {
            ShoppingCartItem item = updateCartItemUseCase.execute(itemId, customerId, request.quantity());
            return ResponseEntity.ok(item);
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        }
    }

    /**
     * Remover item do carrinho
     */
    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> removeItem(@PathVariable UUID itemId) {
        // Em um caso real, obteríamos o customerId do token JWT
        UUID customerId = UUID.fromString("550e8400-e29b-41d4-a716-446655440444"); // Exemplo
        
        try {
            removeItemFromCartUseCase.execute(itemId, customerId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        }
    }

    /**
     * Limpar carrinho
     */
    @DeleteMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> clearCart() {
        // Em um caso real, obteríamos o customerId do token JWT
        UUID customerId = UUID.fromString("550e8400-e29b-41d4-a716-446655440444"); // Exemplo
        
        try {
            clearCartUseCase.execute(customerId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        }
    }
}

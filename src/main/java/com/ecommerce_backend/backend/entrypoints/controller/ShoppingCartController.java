package com.ecommerce_backend.backend.entrypoints.controller;

import com.ecommerce_backend.backend.core.domain.ShoppingCartItem;
import com.ecommerce_backend.backend.core.useCases.cart.*;
import com.ecommerce_backend.backend.entrypoints.dto.*;
import com.ecommerce_backend.backend.infrastructure.security.AuthenticatedUser;
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
    private final AuthenticatedUser authenticatedUser;

    public ShoppingCartController(
            GetCartUseCase getCartUseCase,
            AddItemToCartUseCase addItemToCartUseCase,
            UpdateCartItemUseCase updateCartItemUseCase,
            RemoveItemFromCartUseCase removeItemFromCartUseCase,
            ClearCartUseCase clearCartUseCase,
            AuthenticatedUser authenticatedUser) {
        this.getCartUseCase = getCartUseCase;
        this.addItemToCartUseCase = addItemToCartUseCase;
        this.updateCartItemUseCase = updateCartItemUseCase;
        this.removeItemFromCartUseCase = removeItemFromCartUseCase;
        this.clearCartUseCase = clearCartUseCase;
        this.authenticatedUser = authenticatedUser;
    }


    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<CartResponse> getCart() {
        UUID customerId = getCustomerIdFromToken();
        
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
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ShoppingCartItem> addItem(@RequestBody @Valid AddCartItemRequest request) {
        UUID customerId = getCustomerIdFromToken();
        
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
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<ShoppingCartItem> updateItem(
            @PathVariable UUID itemId,
            @RequestBody @Valid UpdateCartItemRequest request) {
        UUID customerId = getCustomerIdFromToken();
        
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
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Void> removeItem(@PathVariable UUID itemId) {
        UUID customerId = getCustomerIdFromToken();
        
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
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Void> clearCart() {
        UUID customerId = getCustomerIdFromToken();
        
        try {
            clearCartUseCase.execute(customerId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw ex; // Deixa o GlobalExceptionHandler tratar
        }
    }

    /**
     * Método auxiliar para obter o ID do cliente do token JWT.
     * Para usuários ADMIN, retorna um UUID fixo para compatibilidade.
     */
    private UUID getCustomerIdFromToken() {
        if (authenticatedUser.isCustomer()) {
            return authenticatedUser.getCurrentCustomerId();
        } else if (authenticatedUser.isAdmin()) {
            // Para ADMIN, mantemos o UUID fixo por enquanto
            // Em produção, isso deveria ser tratado de forma diferente
            return UUID.fromString("550e8400-e29b-41d4-a716-446655440444");
        } else {
            throw new IllegalStateException("User must be CUSTOMER or ADMIN");
        }
    }
}

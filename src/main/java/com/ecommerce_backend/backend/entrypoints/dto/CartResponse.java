package com.ecommerce_backend.backend.entrypoints.dto;

import com.ecommerce_backend.backend.core.domain.ShoppingCart;
import com.ecommerce_backend.backend.core.domain.ShoppingCartItem;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        ShoppingCart cart,
        List<ShoppingCartItem> items,
        int totalItems,
        BigDecimal totalAmount
) {
    public CartResponse(ShoppingCart cart, List<ShoppingCartItem> items) {
        this(cart, items, 
             items.stream().mapToInt(ShoppingCartItem::quantity).sum(),
             items.stream()
                 .map(ShoppingCartItem::totalPrice)
                 .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}

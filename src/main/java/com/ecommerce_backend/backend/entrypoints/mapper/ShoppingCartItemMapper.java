package com.ecommerce_backend.backend.entrypoints.mapper;

import com.ecommerce_backend.backend.core.domain.ShoppingCartItem;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.ShoppingCartEntity;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.ShoppingCartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ShoppingCartItemMapper {
    ShoppingCartItem toDomain(ShoppingCartItemEntity entity);
    
    @Mapping(target = "shoppingCart", source = "shoppingCartId", qualifiedByName = "uuidToShoppingCartEntity")
    ShoppingCartItemEntity toEntity(ShoppingCartItem domain);
    
    @Named("uuidToShoppingCartEntity")
    default ShoppingCartEntity uuidToShoppingCartEntity(UUID shoppingCartId) {
        if (shoppingCartId == null) {
            return null;
        }
        ShoppingCartEntity entity = new ShoppingCartEntity();
        entity.setId(shoppingCartId);
        return entity;
    }
}

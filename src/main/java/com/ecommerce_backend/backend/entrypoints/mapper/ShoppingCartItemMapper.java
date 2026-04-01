package com.ecommerce_backend.backend.entrypoints.mapper;

import com.ecommerce_backend.backend.core.domain.ShoppingCartItem;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.ShoppingCartItemEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShoppingCartItemMapper {
    ShoppingCartItem toDomain(ShoppingCartItemEntity entity);
    ShoppingCartItemEntity toEntity(ShoppingCartItem domain);
}

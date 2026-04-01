package com.ecommerce_backend.backend.entrypoints.mapper;

import com.ecommerce_backend.backend.core.domain.ShoppingCart;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.ShoppingCartEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {
    ShoppingCart toDomain(ShoppingCartEntity entity);
    ShoppingCartEntity toEntity(ShoppingCart domain);
}

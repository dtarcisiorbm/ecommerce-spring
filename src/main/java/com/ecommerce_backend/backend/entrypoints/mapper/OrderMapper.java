package com.ecommerce_backend.backend.entrypoints.mapper;

import com.ecommerce_backend.backend.core.domain.Order;
import com.ecommerce_backend.backend.core.domain.OrderItem;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.OrderEntity;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    // O MapStruct entende automaticamente como converter a lista de itens
    // se o OrderItemMapper também estiver no contexto.
    Order toDomain(OrderEntity entity);
    OrderEntity toEntity(Order domain);
}

@Mapper(componentModel = "spring")
interface OrderItemMapper {
    OrderItem toDomain(OrderItemEntity entity);
    OrderItemEntity toEntity(OrderItem domain);
}
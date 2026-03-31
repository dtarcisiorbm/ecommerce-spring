package com.ecommerce_backend.backend.entrypoints.mapper;

import com.ecommerce_backend.backend.core.domain.Order;
import com.ecommerce_backend.backend.core.domain.OrderItem;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.OrderEntity;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.OrderItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // Mapeia o customerId da entidade para o id dentro do objeto Customer do domínio
    @Mapping(target = "customer.id", source = "customerId")
    Order toDomain(OrderEntity entity);

    // Mapeia o id do objeto Customer do domínio para o campo customerId da entidade
    @Mapping(target = "customerId", source = "customer.id")
    OrderEntity toEntity(Order domain);
}

@Mapper(componentModel = "spring")
interface OrderItemMapper {
    OrderItem toDomain(OrderItemEntity entity);
    OrderItemEntity toEntity(OrderItem domain);
}
package com.ecommerce_backend.backend.entrypoints.mapper;

import com.ecommerce_backend.backend.core.domain.Product;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toDomain(ProductEntity entity);
    ProductEntity toEntity(Product domain);
}
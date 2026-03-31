package com.ecommerce_backend.backend.entrypoints.mapper;

import com.ecommerce_backend.backend.core.domain.Customer;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.CustomerEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toDomain(CustomerEntity entity);
    CustomerEntity toEntity(Customer domain);
}
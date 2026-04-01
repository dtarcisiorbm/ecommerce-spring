package com.ecommerce_backend.backend.entrypoints.mapper;

import com.ecommerce_backend.backend.core.domain.Payment;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.PaymentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toDomain(PaymentEntity entity);
    PaymentEntity toEntity(Payment domain);
}

package com.ecommerce_backend.backend.entrypoints.mapper;

import com.ecommerce_backend.backend.core.domain.Notification;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.NotificationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    Notification toDomain(NotificationEntity entity);
    NotificationEntity toEntity(Notification domain);
}

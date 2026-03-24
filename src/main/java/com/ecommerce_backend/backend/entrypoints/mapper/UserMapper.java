package com.ecommerce_backend.backend.entrypoints.mapper;

import com.ecommerce_backend.backend.core.domain.User;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // Isso permite que o Spring injete o Mapper
public interface UserMapper {
    User toDomain(UserEntity entity);
    UserEntity toEntity(User domain);
}
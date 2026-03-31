package com.ecommerce_backend.backend.infrastructure.dataprovider.repository;

import com.ecommerce_backend.backend.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    // Método necessário para a autenticação e UserDataProvider
    Optional<UserEntity> findByEmail(String email);



}
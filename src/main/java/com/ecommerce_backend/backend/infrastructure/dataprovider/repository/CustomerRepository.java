package com.ecommerce_backend.backend.infrastructure.dataprovider.repository;

import com.ecommerce_backend.backend.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByEmail(String email);
    Optional<CustomerEntity> findByIdAndActive(@Param("id") UUID id, @Param("active") boolean active);
    
    @Transactional
    @Modifying
    @Query("UPDATE CustomerEntity c SET c.active = false WHERE c.id = :id")
    void softDeleteById(@Param("id") UUID id);
    
    @Query("SELECT c FROM CustomerEntity c WHERE c.active = true")
    org.springframework.data.domain.Page<CustomerEntity> findAllActive(org.springframework.data.domain.Pageable pageable);
}
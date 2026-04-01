package com.ecommerce_backend.backend.infrastructure.dataprovider.repository;

import com.ecommerce_backend.backend.core.domain.PaymentStatus;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByOrderId(UUID orderId);
    
    @Query("SELECT p FROM PaymentEntity p WHERE p.status = :status")
    List<PaymentEntity> findByStatus(@Param("status") PaymentStatus status);
    
    @Query("SELECT p FROM PaymentEntity p WHERE p.createdAt BETWEEN :start AND :end")
    List<PaymentEntity> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT p FROM PaymentEntity p WHERE p.transactionId = :transactionId")
    Optional<PaymentEntity> findByTransactionId(@Param("transactionId") String transactionId);
}

package com.ecommerce_backend.backend.infrastructure.dataprovider.repository;

import com.ecommerce_backend.backend.core.domain.NotificationStatus;
import com.ecommerce_backend.backend.core.domain.NotificationType;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
    
    List<NotificationEntity> findByUserId(UUID userId);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.status = :status")
    List<NotificationEntity> findByStatus(@Param("status") NotificationStatus status);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.type = :type")
    List<NotificationEntity> findByType(@Param("type") NotificationType type);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.createdAt BETWEEN :start AND :end")
    List<NotificationEntity> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT n FROM NotificationEntity n WHERE n.status = :status AND n.createdAt < :retryBefore")
    List<NotificationEntity> findFailedNotificationsForRetry(@Param("status") NotificationStatus status, @Param("retryBefore") LocalDateTime retryBefore);
    
    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.status = :status AND n.type = :type")
    long countByStatusAndType(@Param("status") NotificationStatus status, @Param("type") NotificationType type);
}

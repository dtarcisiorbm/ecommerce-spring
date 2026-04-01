package com.ecommerce_backend.backend.core.gateway;

import com.ecommerce_backend.backend.core.domain.Notification;
import com.ecommerce_backend.backend.core.domain.NotificationType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationGateway {
    
    // Envio de notificações
    Notification sendNotification(UUID userId, String title, String message, NotificationType type, String recipient);
    Notification sendBulkNotification(List<UUID> userIds, String title, String message, NotificationType type);
    
    // Gestão de notificações
    Notification save(Notification notification);
    Optional<Notification> findById(UUID id);
    List<Notification> findByUserId(UUID userId);
    List<Notification> findByStatus(com.ecommerce_backend.backend.core.domain.NotificationStatus status);
    
    // Retentativas
    Notification retryNotification(UUID notificationId);
    void markAsSent(UUID notificationId);
    void markAsFailed(UUID notificationId, String errorMessage);
    
    // Templates
    Notification sendOrderConfirmation(UUID userId, String orderId);
    Notification sendPaymentConfirmation(UUID userId, String orderId, String paymentId);
    Notification sendShippingNotification(UUID userId, String orderId, String trackingNumber);
    Notification sendPasswordReset(UUID userId, String resetToken);
}

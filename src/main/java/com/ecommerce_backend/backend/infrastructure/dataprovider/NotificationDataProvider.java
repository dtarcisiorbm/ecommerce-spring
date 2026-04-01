package com.ecommerce_backend.backend.infrastructure.dataprovider;

import com.ecommerce_backend.backend.core.domain.Notification;
import com.ecommerce_backend.backend.core.domain.NotificationStatus;
import com.ecommerce_backend.backend.core.domain.NotificationType;
import com.ecommerce_backend.backend.core.gateway.NotificationGateway;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.NotificationEntity;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.NotificationRepository;
import com.ecommerce_backend.backend.entrypoints.mapper.NotificationMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class NotificationDataProvider implements NotificationGateway {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public NotificationDataProvider(NotificationRepository notificationRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public Notification sendNotification(UUID userId, String title, String message, NotificationType type, String recipient) {
        NotificationEntity notificationEntity = NotificationEntity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title(title)
                .message(message)
                .type(type)
                .status(NotificationStatus.PENDING)
                .recipient(recipient)
                .build();

        NotificationEntity saved = notificationRepository.save(notificationEntity);
        return notificationMapper.toDomain(saved);
    }

    @Override
    public Notification sendBulkNotification(List<UUID> userIds, String title, String message, NotificationType type) {
        Notification firstNotification = null;
        
        for (UUID userId : userIds) {
            Notification notification = sendNotification(userId, title, message, type, "default@example.com");
            if (firstNotification == null) {
                firstNotification = notification;
            }
        }
        
        return firstNotification;
    }

    @Override
    @Transactional
    public Notification save(Notification notification) {
        NotificationEntity entity = notificationMapper.toEntity(notification);
        NotificationEntity saved = notificationRepository.save(entity);
        return notificationMapper.toDomain(saved);
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return notificationRepository.findById(id)
                .map(notificationMapper::toDomain);
    }

    @Override
    public List<Notification> findByUserId(UUID userId) {
        return notificationRepository.findByUserId(userId)
                .stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status)
                .stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Notification retryNotification(UUID notificationId) {
        Optional<NotificationEntity> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            throw new IllegalArgumentException("Notification not found: " + notificationId);
        }

        NotificationEntity notification = notificationOpt.get();
        notification.setStatus(NotificationStatus.RETRY);
        notification.setErrorMessage(null);

        NotificationEntity saved = notificationRepository.save(notification);
        return notificationMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void markAsSent(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
        });
    }

    @Override
    @Transactional
    public void markAsFailed(UUID notificationId, String errorMessage) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(errorMessage);
            notificationRepository.save(notification);
        });
    }

    @Override
    public Notification sendOrderConfirmation(UUID userId, String orderId) {
        String title = "Confirmação de Pedido #" + orderId;
        String message = "Seu pedido foi confirmado com sucesso.";
        return sendNotification(userId, title, message, NotificationType.EMAIL, "default@example.com");
    }

    @Override
    public Notification sendPaymentConfirmation(UUID userId, String orderId, String paymentId) {
        String title = "Pagamento Confirmado - Pedido #" + orderId;
        String message = "Seu pagamento foi confirmado. ID: " + paymentId;
        return sendNotification(userId, title, message, NotificationType.EMAIL, "default@example.com");
    }

    @Override
    public Notification sendShippingNotification(UUID userId, String orderId, String trackingNumber) {
        String title = "Pedido Enviado - #" + orderId;
        String message = "Seu pedido foi enviado. Rastreio: " + trackingNumber;
        return sendNotification(userId, title, message, NotificationType.EMAIL, "default@example.com");
    }

    @Override
    public Notification sendPasswordReset(UUID userId, String resetToken) {
        String title = "Redefinição de Senha";
        String message = "Token de redefinição: " + resetToken;
        return sendNotification(userId, title, message, NotificationType.EMAIL, "default@example.com");
    }
}

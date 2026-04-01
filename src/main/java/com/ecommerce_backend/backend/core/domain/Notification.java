package com.ecommerce_backend.backend.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record Notification(
        UUID id,
        UUID userId,
        String title,
        String message,
        NotificationType type,
        NotificationStatus status,
        String recipient,
        LocalDateTime createdAt,
        LocalDateTime sentAt,
        String errorMessage
) {
    public Notification {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

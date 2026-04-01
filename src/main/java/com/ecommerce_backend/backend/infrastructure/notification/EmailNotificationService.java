package com.ecommerce_backend.backend.infrastructure.notification;

import com.ecommerce_backend.backend.core.domain.Notification;
import com.ecommerce_backend.backend.core.domain.NotificationStatus;
import com.ecommerce_backend.backend.core.domain.NotificationType;
import com.ecommerce_backend.backend.core.gateway.NotificationGateway;
import com.ecommerce_backend.backend.infrastructure.persistence.entity.NotificationEntity;
import com.ecommerce_backend.backend.infrastructure.dataprovider.repository.NotificationRepository;
import com.ecommerce_backend.backend.entrypoints.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EmailNotificationService implements NotificationGateway {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final JavaMailSender mailSender;

    public EmailNotificationService(
            NotificationRepository notificationRepository,
            NotificationMapper notificationMapper,
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromEmail) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.mailSender = mailSender;
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

        try {
            // Enviar email
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("noreply@ecommerce.com");
            mailMessage.setTo(recipient);
            mailMessage.setSubject(title);
            mailMessage.setText(message);

            mailSender.send(mailMessage);

            // Marcar como enviado
            notificationEntity.setStatus(NotificationStatus.SENT);
            notificationEntity.setSentAt(LocalDateTime.now());

        } catch (Exception e) {
            // Marcar como falha
            notificationEntity.setStatus(NotificationStatus.FAILED);
            notificationEntity.setErrorMessage("Email sending failed: " + e.getMessage());
        }

        NotificationEntity saved = notificationRepository.save(notificationEntity);
        return notificationMapper.toDomain(saved);
    }

    @Override
    public Notification sendBulkNotification(List<UUID> userIds, String title, String message, NotificationType type) {
        // Para notificações em massa, criamos uma entrada para cada usuário
        // Em um caso real, poderíamos usar serviços como SendGrid ou Mailgun para melhor performance
        Notification firstNotification = null;
        
        for (UUID userId : userIds) {
            // Aqui deveríamos buscar o email do usuário
            String userEmail = "user" + userId + "@example.com"; // Exemplo
            
            Notification notification = sendNotification(userId, title, message, type, userEmail);
            if (firstNotification == null) {
                firstNotification = notification;
            }
        }
        
        return firstNotification;
    }

    @Override
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
                .toList();
    }

    @Override
    public List<Notification> findByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status)
                .stream()
                .map(notificationMapper::toDomain)
                .toList();
    }

    @Override
    public Notification retryNotification(UUID notificationId) {
        Optional<NotificationEntity> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            throw new IllegalArgumentException("Notification not found: " + notificationId);
        }

        NotificationEntity notification = notificationOpt.get();
        
        // Resetar status para PENDING e tentar novamente
        notification.setStatus(NotificationStatus.PENDING);
        notification.setErrorMessage(null);

        return sendNotification(notification.getUserId(), notification.getTitle(), 
                notification.getMessage(), notification.getType(), notification.getRecipient());
    }

    @Override
    public void markAsSent(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);
        });
    }

    @Override
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
        String message = String.format("""
                Olá!
                
                Seu pedido #%s foi confirmado com sucesso.
                
                Status: Confirmado
                Data: %s
                
                Acompanhe seu pedido através da nossa plataforma.
                
                Obrigado pela compra!
                """, orderId, LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        // Aqui deveríamos buscar o email do usuário
        String userEmail = "user" + userId + "@example.com"; // Exemplo
        
        return sendNotification(userId, title, message, NotificationType.EMAIL, userEmail);
    }

    @Override
    public Notification sendPaymentConfirmation(UUID userId, String orderId, String paymentId) {
        String title = "Pagamento Confirmado - Pedido #" + orderId;
        String message = String.format("""
                Olá!
                
                Seu pagamento para o pedido #%s foi confirmado.
                
                ID do Pagamento: %s
                Data: %s
                
                Seu pedido está sendo preparado para envio.
                
                Obrigado!
                """, orderId, paymentId, LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        String userEmail = "user" + userId + "@example.com"; // Exemplo
        
        return sendNotification(userId, title, message, NotificationType.EMAIL, userEmail);
    }

    @Override
    public Notification sendShippingNotification(UUID userId, String orderId, String trackingNumber) {
        String title = "Seu Pedido foi Enviado! #" + orderId;
        String message = String.format("""
                Olá!
                
                Seu pedido #%s foi enviado!
                
                Código de Rastreamento: %s
                Data de Envio: %s
                
                Acompanhe sua entrega pelo site dos correios.
                
                Obrigado!
                """, orderId, trackingNumber, LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        String userEmail = "user" + userId + "@example.com"; // Exemplo
        
        return sendNotification(userId, title, message, NotificationType.EMAIL, userEmail);
    }

    @Override
    public Notification sendPasswordReset(UUID userId, String resetToken) {
        String title = "Redefinição de Senha";
        String message = String.format("""
                Olá!
                
                Recebemos uma solicitação para redefinir sua senha.
                
                Token de Redefinição: %s
                Validade: 30 minutos
                
                Se você não solicitou esta redefinição, ignore este email.
                
                Atenciosamente,
                Equipe E-commerce
                """, resetToken);

        String userEmail = "user" + userId + "@example.com"; // Exemplo
        
        return sendNotification(userId, title, message, NotificationType.EMAIL, userEmail);
    }
}

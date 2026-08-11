package com.example.notificationservice.service.impl;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .customerId(request.getCustomerId())
                .orderId(request.getOrderId())
                .type(request.getType())
                .title(request.getTitle())
                .message(request.getMessage())
                .isRead(false)
                .build();

        return mapToResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotification(Long id) {
        return mapToResponse(findNotification(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getCustomerNotifications(Long customerId) {
        return notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long id) {
        Notification notification = findNotification(id);
        notification.setIsRead(true);
        return mapToResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public List<NotificationResponse> markAllAsRead(Long customerId) {
        List<Notification> notifications =
                notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);

        notifications.forEach(notification -> notification.setIsRead(true));

        return notificationRepository.saveAll(notifications)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private Notification findNotification(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Notification not found with ID: " + id));
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .customerId(notification.getCustomerId())
                .orderId(notification.getOrderId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

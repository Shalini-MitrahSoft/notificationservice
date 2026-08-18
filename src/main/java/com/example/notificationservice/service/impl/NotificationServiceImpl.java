package com.example.notificationservice.service.impl;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    // Create notification
    @Override
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {

        Notification notification = new Notification();

        notification.setCustomerId(request.getCustomerId());
        notification.setOrderId(request.getOrderId());
        notification.setType(request.getType());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setIsRead(false);

        Notification savedNotification =
                notificationRepository.save(notification);

        return mapToResponse(savedNotification);
    }

    // Get one notification by ID
    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotification(Long id) {

        Notification notification = findNotification(id);

        return mapToResponse(notification);
    }

    // Get all notifications for a customer
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getCustomerNotifications(Long customerId) {

        List<Notification> notifications =
                notificationRepository
                        .findByCustomerIdOrderByCreatedAtDesc(customerId);

        return notifications.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get all notifications
    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {

        List<Notification> notifications =
                notificationRepository.findAllByOrderByCreatedAtDesc();

        return notifications.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Mark one notification as read
    @Override
    @Transactional
    public NotificationResponse markAsRead(Long id) {

        Notification notification = findNotification(id);
        notification.setIsRead(true);

        Notification updatedNotification = notificationRepository.save(notification);

        return mapToResponse(updatedNotification);
    }

    // Mark all customer notifications as read
    @Override
    @Transactional
    public List<NotificationResponse> markAllAsRead(Long customerId) {

        List<Notification> notifications = notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);

        notifications.forEach(notification -> notification.setIsRead(true));

        List<Notification> updatedNotifications = notificationRepository.saveAll(notifications);

        return updatedNotifications.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Find notification or throw 404 error
    private Notification findNotification(Long id) {

        return notificationRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Notification not found with ID: " + id
                        )
                );
    }

    // Convert Entity into Response DTO
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
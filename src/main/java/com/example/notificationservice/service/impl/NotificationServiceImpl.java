package com.example.notificationservice.service.impl;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Caching(evict = {
            @CacheEvict(cacheNames = "notificationList", allEntries = true),
            @CacheEvict(cacheNames = "customerNotifications", key = "#request.customerId")
    })
    public NotificationResponse createNotification(NotificationRequest request) {

        Notification notification = new Notification();

        notification.setCustomerId(request.getCustomerId());
        notification.setOrderId(request.getOrderId());
        notification.setType(request.getType());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setIsRead(false);

        Notification savedNotification = notificationRepository.save(notification);
        NotificationResponse response = mapToResponse(savedNotification);

        return response;
    }

    // Get one notification by ID
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "notification", key = "#id")
    public NotificationResponse getNotification(Long id) {
        return mapToResponse(findNotification(id));
    }

    // Get all notifications for a customer
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "customerNotifications", key = "#customerId")
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
    @Cacheable(cacheNames = "notificationList", key = "'all'")
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
    @Caching(evict = {
            @CacheEvict(cacheNames = "notification", key = "#id"),
            @CacheEvict(cacheNames = "notificationList", allEntries = true),
            @CacheEvict(cacheNames = "customerNotifications", key = "#result.customerId")
    })
    public NotificationResponse markAsRead(Long id) {

        Notification notification = findNotification(id);
        notification.setIsRead(true);

        Notification updatedNotification = notificationRepository.save(notification);
        NotificationResponse response = mapToResponse(updatedNotification);

        return response;
    }

    // Mark all customer notifications as read
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "notification", allEntries = true),
            @CacheEvict(cacheNames = "notificationList", allEntries = true),
            @CacheEvict(cacheNames = "customerNotifications", key = "#customerId")
    })
    public List<NotificationResponse> markAllAsRead(Long customerId) {

        List<Notification> notifications = notificationRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);

        notifications.forEach(notification -> notification.setIsRead(true));

        List<Notification> updatedNotifications = notificationRepository.saveAll(notifications);
        List<NotificationResponse> response = updatedNotifications.stream()
                .map(this::mapToResponse)
                .toList();

        return response;
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

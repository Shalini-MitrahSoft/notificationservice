package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse createNotification(NotificationRequest request);
    NotificationResponse getNotification(Long id);
    List<NotificationResponse> getCustomerNotifications(Long customerId);
    NotificationResponse markAsRead(Long id);
    List<NotificationResponse> markAllAsRead(Long customerId);
}

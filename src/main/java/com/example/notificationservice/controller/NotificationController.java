package com.example.notificationservice.controller;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.dto.NotificationResponse;
import com.example.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.createNotification(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotification(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotification(id));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getCustomerNotifications(
            @RequestParam Long customerId) {
        return ResponseEntity.ok(notificationService.getCustomerNotifications(customerId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PutMapping("/read-all")
    public ResponseEntity<List<NotificationResponse>> markAllAsRead(
            @RequestParam Long customerId) {
        return ResponseEntity.ok(notificationService.markAllAsRead(customerId));
    }
}

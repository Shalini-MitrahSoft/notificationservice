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
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationRequest request) {

        NotificationResponse response = notificationService.createNotification(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }



    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotification(@PathVariable Long id) {

        NotificationResponse response = notificationService.getNotification(id);
        return ResponseEntity.ok(response);
    }



    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getCustomerNotifications(@RequestParam Long customerId) {

        List<NotificationResponse> responses = notificationService.getCustomerNotifications(customerId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/all")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {

        List<NotificationResponse> responses = notificationService.getAllNotifications();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {

        NotificationResponse response = notificationService.markAsRead(id);
        return ResponseEntity.ok(response);
    }



    @PutMapping("/read-all")
    public ResponseEntity<List<NotificationResponse>> markAllAsRead(@RequestParam Long customerId) {

        List<NotificationResponse> responses = notificationService.markAllAsRead(customerId);
        return ResponseEntity.ok(responses);
    }
}

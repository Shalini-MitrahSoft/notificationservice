package com.example.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    private Long orderId;

    @NotBlank(message = "Notification type is required")
    private String type;


    private String title;


    private String message;
}

package com.example.notificationservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {

    private String eventType;

    private Long orderId;

    private Long customerId;

    private String status;

    private String reason;

    private LocalDateTime timestamp;
}
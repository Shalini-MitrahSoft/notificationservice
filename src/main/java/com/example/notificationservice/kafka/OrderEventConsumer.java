package com.example.notificationservice.kafka;

import com.example.notificationservice.dto.NotificationRequest;
import com.example.notificationservice.event.OrderEvent;
import com.example.notificationservice.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;

    private final NotificationService notificationService;

    @KafkaListener(topics = "${app.kafka.order-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeOrderEvent(String message) {

        log.info("Received order event from Kafka: {}", message);

        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);

            NotificationRequest request = createNotificationRequest(event);

            notificationService.createNotification(request);

            log.info("Notification created successfully: orderId={}, customerId={}, eventType={}", event.getOrderId(),
                    event.getCustomerId(),
                    event.getEventType()
            );

        } catch (JsonProcessingException exception) {

            log.error("Failed to read Kafka order event: message={}", message, exception);

            throw new IllegalArgumentException("Invalid order event", exception);

        } catch (Exception exception) {

            log.error("Failed to process Kafka order event: message={}", message, exception);

            throw exception;
        }
    }

    private NotificationRequest createNotificationRequest(OrderEvent event) {

        NotificationRequest request = new NotificationRequest();

        request.setCustomerId(event.getCustomerId());

        request.setOrderId(event.getOrderId());

        request.setType(event.getEventType());

        if ("ORDER_CONFIRMED".equals(event.getEventType())) {

            request.setTitle("Order confirmed");

            request.setMessage("Your order " + event.getOrderId() + " has been confirmed.");

        } else if ("ORDER_REJECTED".equals(
                event.getEventType()
        )) {

            request.setTitle("Order rejected");

            request.setMessage("Your order " + event.getOrderId() + " was rejected. Reason: " + event.getReason());

        } else if ("ORDER_CANCELLED".equals(
                event.getEventType()
        )) {

            request.setTitle("Order cancelled");

            request.setMessage("Your order " + event.getOrderId() + " has been cancelled.");

        } else {

            request.setTitle("Order status updated");

            request.setMessage("Order " + event.getOrderId() + " status changed to " + event.getStatus()
            );
        }

        return request;
    }
}
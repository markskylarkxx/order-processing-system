package com.order_service.application.service;

import com.order_service.application.dto.OrderNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendOrderNotification(OrderNotification notification) {
        messagingTemplate.convertAndSend("/topic/orders", notification);
    }
}

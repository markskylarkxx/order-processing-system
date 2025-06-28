package com.order_service.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderNotification {
    private Long orderId;
    private String status;
    private Integer productId;
    private Integer quantity;
}

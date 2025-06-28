package com.order_service.application.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class OrderDto {

    private Long id;

    private Integer productId;
    private int quantity;
    private String status;
}

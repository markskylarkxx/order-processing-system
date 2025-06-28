package com.order_service.presentation.request;


import lombok.Data;

@Data
public class CreateOrder {

    private  Integer productId;
    private  Integer quantity;
}

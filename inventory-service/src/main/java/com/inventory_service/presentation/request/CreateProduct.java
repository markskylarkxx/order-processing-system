package com.inventory_service.presentation.request;

import lombok.Data;

@Data
public class CreateProduct {


    private String name;

    private Integer stockQuantity;
}

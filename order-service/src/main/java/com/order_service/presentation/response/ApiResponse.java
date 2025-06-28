package com.order_service.presentation.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {

    private int status;
    private String message;
    private boolean successful;
    private T data;

}


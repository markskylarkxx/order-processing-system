package com.order_service.presentation.controller;



import com.order_service.presentation.response.ApiResponse;
import com.order_service.application.dto.OrderDto;
import com.order_service.application.service.OrderService;
import com.order_service.domain.Order;
import com.order_service.presentation.request.CreateOrder;
//import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(@RequestBody CreateOrder request) {
        ApiResponse<OrderDto> response = orderService.createOrder(request);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping
 //   @Operation(summary = "Get all orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    //@Operation(summary = "Get order by ID")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }
}

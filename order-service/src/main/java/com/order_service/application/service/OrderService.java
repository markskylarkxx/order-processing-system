package com.order_service.application.service;


import com.inventory_service.grpc.*;
import com.order_service.presentation.response.ApiResponse;
import com.order_service.application.dto.OrderDto;
import com.order_service.application.dto.OrderNotification;
import com.order_service.domain.Order;
import com.order_service.domain.OrderRepository;
import com.order_service.presentation.request.CreateOrder;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub;
    private final OrderNotificationService notificationService;

    @Transactional
    public ApiResponse<OrderDto> createOrder(CreateOrder request) {
        try {
            if (request == null || request.getProductId() == null || request.getQuantity() == null) {
                return ApiResponse.<OrderDto>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Invalid request: productId and quantity are required.")
                        .successful(false)
                        .build();
            }

            StockRequest stockRequest = StockRequest.newBuilder()
                    .setItemId(request.getProductId())
                    .build();

            StockResponse stockResponse = inventoryStub.checkStock(stockRequest);
//todo: update the inventory after successful order, paginate the order the order that is coming

            if (stockResponse.getQuantity() < request.getQuantity()) {

                System.out.println("Insufficient stock for product " + request.getProductId());

                return ApiResponse.<OrderDto>builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("Insufficient stock for product " + request.getProductId())
                        .successful(false)
                        .build();
            }

            Order order = Order.builder()
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .status("CONFIRMED")
                    .build();

            Order savedOrder = orderRepository.save(order);

            //TOdo reduce stock her:

            ReduceStockRequest reduceStockRequest = ReduceStockRequest.newBuilder().
                    setItemId(request.getProductId()).setQuantity(request.getQuantity()).build();
            ReduceStockResponse reduceStockResponse = inventoryStub.reduceStock(reduceStockRequest);
               if (!reduceStockResponse.getSuccess()){
                   throw  new RuntimeException("Fai to rdeuce stock"  + reduceStockResponse.getMessage());
               }
            notificationService.sendOrderNotification(
                    new OrderNotification(
                            savedOrder.getId(),
                            savedOrder.getStatus(),
                            savedOrder.getProductId(),
                            savedOrder.getQuantity()
                    )
            );
            OrderDto orderDto = mapToDto(order);

            return ApiResponse.<OrderDto>builder()
                    .status(HttpStatus.CREATED.value())
                    .message("Order created successfully.")
                    .successful(true)
                    .data(orderDto)
                    .build();

        } catch (Exception ex) {
            return ApiResponse.<OrderDto>builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("An unexpected error occurred: " + ex.getMessage())
                    .successful(false)
                    .build();
        }
    }
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + id));
    }

    public Page<Order> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return orderRepository.findAll(pageable);
    }



    private OrderDto mapToDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .build();
    }

}

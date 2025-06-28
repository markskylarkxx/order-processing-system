package com.order_service.application.service;


import com.inventory_service.grpc.InventoryServiceGrpc;
import com.inventory_service.grpc.StockRequest;
import com.inventory_service.grpc.StockResponse;
import com.order_service.presentation.response.ApiResponse;
import com.order_service.application.dto.OrderDto;
import com.order_service.application.dto.OrderNotification;
import com.order_service.domain.Order;
import com.order_service.domain.OrderRepository;
import com.order_service.presentation.request.CreateOrder;
import lombok.RequiredArgsConstructor;
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


//    @Transactional
//    public ApiResponse<OrderDto> createOrder(CreateOrder request) {
//        try {
//            if (request == null || request.getProductId() == null || request.getQuantity() == null) {
//                return ApiResponse.<OrderDto>builder()
//                        .status(HttpStatus.BAD_REQUEST.value())
//                        .message("Invalid request: productId and quantity are required.")
//                        .successful(false)
//                        .build();
//            }
//
//            // check stock via gRPC
//            StockRequest stockRequest = StockRequest.newBuilder()
//                    .setItemId(request.getProductId()) // corrected field name
//                    .build();
//
//            StockResponse stockResponse = inventoryStub.checkStock(stockRequest);
//
//            if (stockResponse.getQuantity() < request.getQuantity()) {
//                return ApiResponse.<OrderDto>builder()
//                        .status(HttpStatus.BAD_REQUEST.value())
//                        .message("Insufficient stock for product " + request.getProductId())
//                        .successful(false)
//                        .build();
//            }
//
//            // persist order
//            Order order = Order.builder()
//                    .productId(request.getProductId())
//                    .quantity(request.getQuantity())
//                    .status("CONFIRMED")
//                    .build();
//
//            orderRepository.save(order);
//
//            OrderDto dto = mapToDto(order);
//
//            return ApiResponse.<OrderDto>builder()
//                    .status(HttpStatus.CREATED.value())
//                    .message("Order created successfully.")
//                    .successful(true)
//                    .data(dto)
//                    .build();
//
//        } catch (Exception ex) {
//            return ApiResponse.<OrderDto>builder()
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                    .message("An unexpected error occurred: " + ex.getMessage())
//                    .successful(false)
//                    .build();
//        }
//    }

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

            if (stockResponse.getQuantity() < request.getQuantity()) {
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

            notificationService.sendOrderNotification(
                    new OrderNotification(
                            savedOrder.getId(),
                            savedOrder.getStatus(),
                            savedOrder.getProductId(),
                            savedOrder.getQuantity()
                    )
            );

            OrderDto dto = mapToDto(savedOrder);

            return ApiResponse.<OrderDto>builder()
                    .status(HttpStatus.CREATED.value())
                    .message("Order created successfully.")
                    .successful(true)
                    .data(dto)
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

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
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

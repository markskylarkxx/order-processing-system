package com.order_service.service;

import com.inventory_service.grpc.InventoryServiceGrpc;
import com.inventory_service.grpc.StockRequest;
import com.inventory_service.grpc.StockResponse;
import com.order_service.application.dto.OrderDto;
import com.order_service.application.service.OrderNotificationService;
import com.order_service.application.service.OrderService;
import com.order_service.domain.Order;
import com.order_service.domain.OrderRepository;
import com.order_service.presentation.request.CreateOrder;
import com.order_service.presentation.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderNotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrder_successful() {
        CreateOrder request = new CreateOrder();
        request.setProductId(1);
        request.setQuantity(5);

        StockResponse mockResponse = StockResponse.newBuilder()
                .setQuantity(10)
                .build();

        when(inventoryStub.checkStock(any(StockRequest.class))).thenReturn(mockResponse);

        Order savedOrder = Order.builder()
                .id(123L)
                .productId(1)
                .quantity(5)
                .status("CONFIRMED")
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        ApiResponse<OrderDto> response = orderService.createOrder(request);

        assertTrue(response.isSuccessful());
        assertEquals(201, response.getStatus());
        assertNotNull(response.getData());
        assertEquals("Order created successfully.", response.getMessage());

        verify(inventoryStub, times(1)).checkStock(any(StockRequest.class));
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(notificationService, times(1)).sendOrderNotification(any());
    }

    @Test
    void createOrder_insufficientStock() {
        CreateOrder request = new CreateOrder();
        request.setProductId(1);
        request.setQuantity(5);

        StockResponse mockResponse = StockResponse.newBuilder()
                .setQuantity(2)
                .build();

        when(inventoryStub.checkStock(any(StockRequest.class))).thenReturn(mockResponse);

        ApiResponse<OrderDto> response = orderService.createOrder(request);

        assertFalse(response.isSuccessful());
        assertEquals(400, response.getStatus());
        assertEquals("Insufficient stock for product 1", response.getMessage());

        verify(inventoryStub, times(1)).checkStock(any(StockRequest.class));
        verify(orderRepository, never()).save(any(Order.class));
        verify(notificationService, never()).sendOrderNotification(any());
    }

    @Test
    void createOrder_invalidRequest() {
        CreateOrder request = new CreateOrder();

        ApiResponse<OrderDto> response = orderService.createOrder(request);

        assertFalse(response.isSuccessful());
        assertEquals(400, response.getStatus());
        assertEquals("Invalid request: productId and quantity are required.", response.getMessage());

        verify(inventoryStub, never()).checkStock(any(StockRequest.class));
        verify(orderRepository, never()).save(any(Order.class));
        verify(notificationService, never()).sendOrderNotification(any());
    }

    @Test
    void createOrder_grpcThrowsException() {
        CreateOrder request = new CreateOrder();
        request.setProductId(1);
        request.setQuantity(5);

        when(inventoryStub.checkStock(any(StockRequest.class)))
                .thenThrow(new RuntimeException("gRPC error"));

        ApiResponse<OrderDto> response = orderService.createOrder(request);

        assertFalse(response.isSuccessful());
        assertEquals(500, response.getStatus());
        assertTrue(response.getMessage().contains("An unexpected error occurred"));

        verify(inventoryStub, times(1)).checkStock(any(StockRequest.class));
        verify(orderRepository, never()).save(any(Order.class));
        verify(notificationService, never()).sendOrderNotification(any());
    }

    @Test
    void getOrderById_found() {
        Order order = Order.builder()
                .id(123L)
                .productId(1)
                .quantity(5)
                .status("CONFIRMED")
                .build();

        when(orderRepository.findById(123L)).thenReturn(Optional.of(order));

        Order found = orderService.getOrderById(123L);

        assertNotNull(found);
        assertEquals(123L, found.getId());
        assertEquals(1, found.getProductId());
        assertEquals(5, found.getQuantity());
        assertEquals("CONFIRMED", found.getStatus());

        verify(orderRepository, times(1)).findById(123L);
    }

    @Test
    void getOrderById_notFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            orderService.getOrderById(999L);
        });

        assertEquals("Order not found with id: 999", ex.getMessage());

        verify(orderRepository, times(1)).findById(999L);
    }
    @Test
    void getAllOrders_returnsPageOfOrders() {
        List<Order> orders = Arrays.asList(
                Order.builder().id(1L).productId(1).quantity(10).status("CONFIRMED").build(),
                Order.builder().id(2L).productId(2).quantity(20).status("CONFIRMED").build()
        );
        Page<Order> page = new PageImpl<>(orders);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        when(orderRepository.findAll(pageable)).thenReturn(page);

        Page<Order> result = orderService.getAllOrders(0, 10);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(2L, result.getContent().get(1).getId());

        verify(orderRepository, times(1)).findAll(pageable);
    }

}

package com.inventory_service.application.service;

import com.inventory_service.grpc.InventoryServiceGrpc;



import com.inventory_service.domain.Product;
import com.inventory_service.domain.ProductRepository;

import com.inventory_service.grpc.StockRequest;
import com.inventory_service.grpc.StockResponse;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class InventoryServiceImpl extends InventoryServiceGrpc.InventoryServiceImplBase {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void checkStock(StockRequest request, StreamObserver<StockResponse> responseObserver) {

        Product product = productRepository.findById(request.getItemId()).orElse(null);

        int quantity = (product != null) ? product.getStockQuantity() : 0;

        StockResponse response = StockResponse.newBuilder()
                .setQuantity(quantity)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
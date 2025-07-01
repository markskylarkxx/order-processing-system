package com.inventory_service.application.service;

import com.inventory_service.grpc.*;
import com.inventory_service.domain.Product;
import com.inventory_service.domain.ProductRepository;

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

        // reduce the product quantity
        long productRemaining =  product.getStockQuantity() - request.getQuantity();
        product.setStockQuantity((int) productRemaining);

        productRepository.save(product);
        System.out.println("Quantity " + quantity);
        StockResponse response = StockResponse.newBuilder()
                .setQuantity(quantity)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reduceStock(ReduceStockRequest request, StreamObserver<ReduceStockResponse> responseObserver) {
         Product product = productRepository.findById(request.getItemId()).orElse(null);
         if (product == null){
             responseObserver.onNext(ReduceStockResponse.newBuilder().setSuccess(false).setMessage("Product not found").build());

           responseObserver.onCompleted();
           return;
         }

         if (product.getStockQuantity() < request.getQuantity() ){
             responseObserver.onNext(ReduceStockResponse.newBuilder().setSuccess(false).setMessage("insufficient stock").build());
             responseObserver.onCompleted();
             return;
         }

          product.setStockQuantity(product.getStockQuantity() - request.getQuantity());

         productRepository.save(product);
         responseObserver.onNext(ReduceStockResponse.newBuilder().setSuccess(true).setMessage("stocke reduvcsd").build());
         responseObserver.onCompleted();
    }


}
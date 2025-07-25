package com.order_service.config;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

        @Bean   //for local host
        public ManagedChannel inventoryChannel() {
        return ManagedChannelBuilder
                .forAddress("127.0.0.1", 9090)
                .usePlaintext()
                .build();
    }
//    @Bean
//    public ManagedChannel inventoryChannel() {
//        System.out.println("Creating gRPC channel to inventory-service:9090");
//
//        return ManagedChannelBuilder
//                .forAddress("inventory-service", 9090)
//                .usePlaintext()
//                .build();
//    }

    @Bean
    public com.inventory_service.grpc.InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub(ManagedChannel inventoryChannel) {
        return com.inventory_service.grpc.InventoryServiceGrpc.newBlockingStub(inventoryChannel);
    }
}

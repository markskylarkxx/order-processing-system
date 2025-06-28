package com.inventory_service.application.service;

import com.inventory_service.presentation.response.ApiResponse;
import com.inventory_service.domain.Product;
import com.inventory_service.domain.ProductRepository;
import com.inventory_service.presentation.request.CreateProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private  final ProductRepository productRepository;
    public ApiResponse<List<Product>> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();
            return ApiResponse.<List<Product>>builder()
                    .status(200)
                    .message("Products fetched successfully")
                    .successful(true)
                    .data(products)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<Product>>builder()
                    .status(500)
                    .message("Failed to fetch products: " + e.getMessage())
                    .successful(false)
                    .data(null)
                    .build();
        }
    }

    public Product creatProduct(CreateProduct createProduct) {

      Product product = new Product();
         product.setName(createProduct.getName());
         product.setStockQuantity(createProduct.getStockQuantity());
         return  productRepository.save(product);
    }
}

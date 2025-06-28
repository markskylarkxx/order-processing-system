package com.inventory_service.presentation.controller;

import com.inventory_service.presentation.response.ApiResponse;
import com.inventory_service.application.service.ProductService;
import com.inventory_service.domain.Product;
import com.inventory_service.presentation.request.CreateProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        ApiResponse<List<Product>> response = productService.getAllProducts();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping
    public  ResponseEntity<Product> createProduct(@RequestBody CreateProduct createProduct){
           Product product = productService.creatProduct(createProduct);
           return ResponseEntity.ok(product);
    }

}

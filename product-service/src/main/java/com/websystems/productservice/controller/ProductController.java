package com.websystems.productservice.controller;

import com.websystems.productservice.dto.ProductRequest;
import com.websystems.productservice.dto.ProductResponse;
import com.websystems.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequest productrequest) {
        productService.createProduct(productrequest);
    }

    @GetMapping
    public List<ProductResponse> getProducts() {
        return productService.getProducts();
    }


}

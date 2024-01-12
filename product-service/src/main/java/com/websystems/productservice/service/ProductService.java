package com.websystems.productservice.service;

import com.websystems.productservice.dto.ProductRequest;
import com.websystems.productservice.dto.ProductResponse;
import com.websystems.productservice.model.Product;
import com.websystems.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public void createProduct(ProductRequest productRequest)
    {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info("Created Product in Service Class {}", product);
    }

    public List<ProductResponse> getProducts() {
        List<Product> products = productRepository.findAll(); //products From DB
        List<ProductResponse> productResponses = new ArrayList<>(); //ProductResponseToBe returned

        for(Product product :  products) {
            ProductResponse productResponse =  ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .build();

            productResponses.add(productResponse);
        }

        return productResponses;
    }
}

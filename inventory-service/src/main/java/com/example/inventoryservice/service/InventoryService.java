package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryResponse;

import com.example.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
@SneakyThrows
    @Transactional(readOnly = true)
    public List<InventoryResponse> isSkuCodeExists(List<String> skuCode) {
        log.info("Web Started");
Thread.sleep(10000);
        log.info("Web Ended");
        return inventoryRepository.findBySkuCodeIn(skuCode)
                .stream()
                .map(inventory -> InventoryResponse.builder()
                          .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity()>0)
                        .build())
                .toList();
    }
}

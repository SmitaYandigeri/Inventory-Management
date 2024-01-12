package com.websystems.orderservice.service;

import com.websystems.orderservice.dto.InventoryResponse;
import com.websystems.orderservice.dto.OrderLineItemsDto;
import com.websystems.orderservice.dto.OrderRequest;
import com.websystems.orderservice.model.Order;
import com.websystems.orderservice.model.OrderLineItems;
import com.websystems.orderservice.repository.OrderRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrder_number(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = new ArrayList<>();
        List<OrderLineItemsDto> orderLineItemsDtoList = orderRequest.getOrderLineItemsDtoList();
        for (OrderLineItemsDto i : orderLineItemsDtoList) {
            OrderLineItems orderLineItemsList = new OrderLineItems();
            orderLineItemsList.setQuantity(i.getQuantity());
            orderLineItemsList.setPrice(i.getPrice());
            orderLineItemsList.setSkuCode(i.getSkuCode());
            orderLineItems.add(orderLineItemsList);
        }
        order.setOrderLineItemsList(orderLineItems);
        //1. Collect all the skucodes from Order object

        List<String> skucodes = order.getOrderLineItemsList()
                .stream()
                .map(orderLineItems1 -> orderLineItems1.getSkuCode())
                .toList();

        log.info("checking inventory for Skucodes {}",skucodes);
        //2
        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");
        try( Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())){
            InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skucodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            log.info("Received inventory response for requested skucodes {}",inventoryResponsesArray);
            boolean result = Arrays.stream(inventoryResponsesArray)
                    .allMatch(inventoryResponse -> inventoryResponse.getIsInStock());

            log.info("The result {}",result);
            if (result) {
                orderRepository.save(order);
                return "Order Placed successfully";
            } else {
                throw new IllegalArgumentException("One/More Product is out of stock, please try again later");
            }

        }
        finally
        {
            inventoryServiceLookup.end();
        }

        //extract the skucodes from order

        //call the inventory for skucodes to check if inventory has stock

        //if inventory exists
        //orderRepository.save(order);
        // else throw error
    }

}

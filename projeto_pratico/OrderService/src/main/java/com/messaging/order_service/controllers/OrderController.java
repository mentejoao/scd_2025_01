package com.messaging.order_service.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.messaging.order_service.dtos.ItemOrderDTO;
import com.messaging.order_service.dtos.OrderDTO;
import com.messaging.order_service.services.OrderService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/orders")
@Validated

public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> createAndSendOrder(@Valid @RequestBody List<ItemOrderDTO> entity) {
        OrderDTO order = orderService.placeNewOrder(entity);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.orderId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }
    
}

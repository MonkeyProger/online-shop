package org.scenter.onlineshop.controllers;

import org.scenter.onlineshop.requests.OrderRequest;
import org.scenter.onlineshop.responses.MessageResponse;
import org.scenter.onlineshop.responses.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class ShopController {
    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = new OrderResponse();



        return ResponseEntity.ok(orderResponse);
    }

    // 1. email
    // 2. Arr[SaleProduct]

}

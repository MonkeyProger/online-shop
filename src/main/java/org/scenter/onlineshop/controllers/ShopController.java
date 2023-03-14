package org.scenter.onlineshop.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.requests.CloseOrderRequest;
import org.scenter.onlineshop.requests.PlaceOrderRequest;
import org.scenter.onlineshop.responses.MessageResponse;
import org.scenter.onlineshop.services.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/api/cart")
@AllArgsConstructor
public class ShopController {
    private ShopService shopService;
    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody PlaceOrderRequest placeOrderRequest) {
        return shopService.placeOrder(placeOrderRequest);
    }

    @DeleteMapping("/closeOrder")
    public ResponseEntity<?> closeOrder(@Valid @RequestBody CloseOrderRequest closeOrderRequest) {
        return shopService.closeOrder(closeOrderRequest);
    }

    @GetMapping("/getUserActiveOrders/{userEmail}")
    public ResponseEntity<?> getUserActiveOrders(@PathVariable String userEmail) {
        if (!shopService.isAuthorized(userEmail)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Access denied: Not enough rights for this action!"));
        }
        return ResponseEntity.ok(shopService.getActiveUserOrders(userEmail));
    }

    @GetMapping("/getUserClosedOrders/{userEmail}")
    public ResponseEntity<?> getUserClosedOrders(@PathVariable String userEmail){
        if (!shopService.isAuthorized(userEmail)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Access denied: Not enough rights for this action!"));
        }
        return ResponseEntity.ok(shopService.getClosedUserOrders(userEmail));
    }
}

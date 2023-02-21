package org.scenter.onlineshop.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.requests.CloseOrderRequest;
import org.scenter.onlineshop.requests.PlaceOrderRequest;
import org.scenter.onlineshop.services.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


    @GetMapping("/saleProducts")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getSaleProducts(){
        return ResponseEntity.ok().body(shopService.getAllSaleProducts());
    }

    @GetMapping("/allOrders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllOrders(){
        return ResponseEntity.ok().body(shopService.getAllOrders());
    }
    @GetMapping("/allOrders={email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllOrdersByEmail(@PathVariable String email){
        return ResponseEntity.ok().body(shopService.getAllOrdersByEmail(email));
    }

}

package org.scenter.onlineshop.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.common.requests.CloseOrderRequest;
import org.scenter.onlineshop.common.requests.PlaceOrderRequest;
import org.scenter.onlineshop.service.services.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/api/cart")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*", maxAge = 3600)
public class ShopController {

    private final ShopService shopService;

    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody PlaceOrderRequest placeOrderRequest) throws MessagingException {
        return shopService.placeOrder(placeOrderRequest);
    }

    @DeleteMapping("/closeOrder")
    public ResponseEntity<?> closeOrder(@Valid @RequestBody CloseOrderRequest closeOrderRequest) throws AccessException {
        return shopService.closeOrder(closeOrderRequest);
    }

    @GetMapping("/getUserActiveOrders/{userEmail}")
    public ResponseEntity<?> getUserActiveOrders(@PathVariable String userEmail) throws AccessException {
        if (!shopService.isAuthorized(userEmail)) {
            throw new AccessException("Access denied: Not enough rights for this action!");
        }
        return ResponseEntity.ok(shopService.getActiveUserOrders(userEmail));
    }

    @GetMapping("/getUserClosedOrders/{userEmail}")
    public ResponseEntity<?> getUserClosedOrders(@PathVariable String userEmail) throws AccessException {
        if (!shopService.isAuthorized(userEmail)) {
            throw new AccessException("Access denied: Not enough rights for this action!");
        }
        return ResponseEntity.ok(shopService.getClosedUserOrders(userEmail));
    }
}

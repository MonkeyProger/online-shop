package org.scenter.onlineshop.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.Ordering;
import org.scenter.onlineshop.domain.SaleProduct;
import org.scenter.onlineshop.repo.OrderingRepo;
import org.scenter.onlineshop.repo.UserRepo;
import org.scenter.onlineshop.requests.CloseOrderRequest;
import org.scenter.onlineshop.requests.PlaceOrderRequest;
import org.scenter.onlineshop.responses.MessageResponse;
import org.scenter.onlineshop.responses.OrderResponse;
import org.scenter.onlineshop.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/cart")
@AllArgsConstructor
public class ShopController {

    private UserRepo userRepo;
    private OrderingRepo orderingRepo;
    private OrderService orderService;
    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody PlaceOrderRequest placeOrderRequest) {

        OrderResponse orderResponse = new OrderResponse();
        List<SaleProduct> cart = placeOrderRequest.getOrder();
        float cartCost = orderService.getCartCost(cart);

        Optional<AppUser> user = userRepo.findByEmail(placeOrderRequest.getEmail());
        if (user.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is not presented!"));
        }
        Ordering order = new Ordering(null, user.get().getEmail(), cart,cartCost);
        orderService.saveSaleProducts(cart);
        orderService.saveOrdering(order);
        log.info("Order processed successfully..");

        orderResponse.setAmount(cartCost);
        orderResponse.setDate(new Date().toString());
        orderResponse.setId(order.getId());

        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping("/closeOrder")
    public ResponseEntity<?> closeOrder(@Valid @RequestBody CloseOrderRequest closeOrderRequest) {
        Optional<Ordering> order = orderingRepo.findById(closeOrderRequest.getOrderId());
        if (order.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Order is not presented!"));
        }
        Long orderId = order.get().getId();
        orderService.closeOrdering(orderId);
        log.info("Order closed successfully..");
        return ResponseEntity.ok(new MessageResponse("Order "+orderId.toString()+" closed successfully"));
    }

    @GetMapping("/allOrders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllOrders(){
        return ResponseEntity.ok().body(orderService.getAllOrders());
    }
    @GetMapping("/allOrders={email}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllOrdersByEmail(@PathVariable String email){
        return ResponseEntity.ok().body(orderService.getAllOrdersByEmail(email));
    }

}

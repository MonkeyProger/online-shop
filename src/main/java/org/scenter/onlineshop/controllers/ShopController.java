package org.scenter.onlineshop.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.Ordering;
import org.scenter.onlineshop.domain.SaleProduct;
import org.scenter.onlineshop.repo.UserRepo;
import org.scenter.onlineshop.requests.OrderRequest;
import org.scenter.onlineshop.responses.MessageResponse;
import org.scenter.onlineshop.responses.OrderResponse;
import org.scenter.onlineshop.services.OrderService;
import org.scenter.onlineshop.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/cart")
@AllArgsConstructor
public class ShopController {

    @Autowired
    private UserRepo userRepo;
    private OrderService orderService;
    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderRequest orderRequest) {

        OrderResponse orderResponse = new OrderResponse();
        List<SaleProduct> cart = orderRequest.getOrder();
        float cartCost = orderService.getCartCost(cart);

        Optional<AppUser> user = userRepo.findByEmail(orderRequest.getEmail());
        if (user.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is not presented!"));
        }
        Ordering order = new Ordering(null, user.get(), cart,cartCost);
        orderService.saveSaleProducts(cart);
        orderService.saveOrdering(order);
        log.info("Order processed successfully..");

        orderResponse.setAmount(cartCost);
        orderResponse.setDate(new Date().toString());

        return ResponseEntity.ok(orderResponse);
    }

}

package org.scenter.onlineshop.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.Ordering;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.domain.SaleProduct;
import org.scenter.onlineshop.repo.OrderingRepo;
import org.scenter.onlineshop.repo.ProductRepo;
import org.scenter.onlineshop.repo.SaleProductRepo;
import org.scenter.onlineshop.repo.UserRepo;
import org.scenter.onlineshop.requests.CloseOrderRequest;
import org.scenter.onlineshop.requests.PlaceOrderRequest;
import org.scenter.onlineshop.responses.MessageResponse;
import org.scenter.onlineshop.responses.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class ShopService {

    private UserRepo userRepo;
    private OrderingRepo orderingRepo;
    private SaleProductRepo saleProductRepo;
    private ProductRepo productRepo;
    private EmailService emailService;

    public float getCartCost(Set<SaleProduct> productSet){
        float totalSum = 0f;
        float currentSum;
        int newAmount = 0;

        for (SaleProduct cartProduct : productSet) {
            Long productId = cartProduct.getProductId();
            Optional<Product> repoProduct = productRepo.findById(productId);
            if (repoProduct.isPresent()){
                Product product = repoProduct.get();
                if (product.getAmount() < cartProduct.getAmount()){
                    currentSum = product.getPrice() * product.getAmount();
                    cartProduct.setAmount(product.getAmount());
                    saleProductRepo.save(cartProduct);
                } else {
                    currentSum = product.getPrice() * cartProduct.getAmount();
                    newAmount = product.getAmount() - cartProduct.getAmount();
                }
                totalSum += currentSum;
                product.setAmount(newAmount);
                newAmount = 0;
                productRepo.save(product);
            }
        }
        return totalSum;
    }

    public boolean isAuthorized(String email){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Objects.equals(email, username);
    }

    public ResponseEntity<?> placeOrder(PlaceOrderRequest placeOrderRequest) {
        if (!isAuthorized(placeOrderRequest.getEmail())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Access denied: Not enough rights for this action!"));
        }
        Optional<AppUser> user = userRepo.findByEmail(placeOrderRequest.getEmail());
        if (user.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is not presented!"));
        }
        Set<SaleProduct> cart = placeOrderRequest.getOrder();
        float cartCost = getCartCost(cart);

        Ordering order = new Ordering(null, user.get().getEmail(), cart, cartCost);
        saveSaleProducts(cart);
        saveOrdering(order);
        log.info(emailService.sendOrderToEmail(order.getCart(),cartCost,user.get().getEmail()));
        log.info("Order processed successfully..");

        OrderResponse orderResponse = new OrderResponse(cartCost,new Date().toString(),order.getId());
        return ResponseEntity.ok(orderResponse);
    }

    public ResponseEntity<?> closeOrder(CloseOrderRequest closeOrderRequest) {
        Optional<Ordering> order = orderingRepo.findById(closeOrderRequest.getOrderId());
        if (order.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Order is not presented!"));
        }
        Ordering ordering = order.get();
        if (!isAuthorized(ordering.getUserEmail())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Access denied: Not enough rights for this action!"));
        }

        closeOrdering(ordering.getId());
        log.info("Order closed successfully..");
        return ResponseEntity.ok(new MessageResponse("Order "+ordering.getId().toString()+" closed successfully"));
    }

    public List<Ordering> getAllOrders() {return orderingRepo.findAll();}
    public List<SaleProduct> getAllSaleProducts() {return saleProductRepo.findAll();}
    public List<Ordering> getAllOrdersByEmail(String email) {return orderingRepo.findAllByUserEmail(email);}

    @Transactional
    public void saveSaleProducts(Set<SaleProduct> cart){
        saleProductRepo.saveAll(cart);
    }
    @Transactional
    public void saveOrdering(Ordering order){
        orderingRepo.save(order);
    }
    @Transactional
    public void closeOrdering(Long orderId){
        orderingRepo.deleteById(orderId);
    }
}

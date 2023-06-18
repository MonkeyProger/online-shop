package org.scenter.onlineshop.service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.Ordering;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.domain.SaleProduct;
import org.scenter.onlineshop.repo.OrderingRepo;
import org.scenter.onlineshop.repo.SaleProductRepo;
import org.scenter.onlineshop.repo.UserRepo;
import org.scenter.onlineshop.common.requests.CloseOrderRequest;
import org.scenter.onlineshop.common.requests.PlaceOrderRequest;
import org.scenter.onlineshop.common.responses.MessageResponse;
import org.scenter.onlineshop.common.responses.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ShopService {

    private final UserRepo userRepo;
    private final OrderingRepo orderingRepo;
    private final SaleProductRepo saleProductRepo;
    private final StockService stockService;
    private final EmailService emailService;

    private Set<Product> isCartInStock(Set<SaleProduct> productSet){
        Set<Product> notAvailableProducts = new HashSet<>();
        Set<Product> productForSave = new HashSet<>();
        for (SaleProduct cartProduct : productSet) {
            int newAmount;
            Long productId = cartProduct.getProductId();
            Product product = stockService.getProductById(productId);
            if (product.getAmount() < cartProduct.getAmount() || product.getAmount().equals(0)){
                notAvailableProducts.add(product);
            } else {
                newAmount = product.getAmount() - cartProduct.getAmount();
                product.setAmount(newAmount);
                productForSave.add(product);
            }
        }
        if (notAvailableProducts.isEmpty()) stockService.saveAllProducts(productForSave);
        return notAvailableProducts;
    }

    public boolean isAuthorized(String email){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Objects.equals(email, username);
    }

    public Ordering getOrderById(Long orderId) {
        Optional<Ordering> ordering = orderingRepo.findById(orderId);
        if (!ordering.isPresent()){
            log.error("Order with id " + orderId + "not found");
            throw new NoSuchElementException("Order with id " + orderId + "not found");
        }
        return ordering.get();
    }

    public ResponseEntity<?> placeOrder(PlaceOrderRequest placeOrderRequest) {
        if (!isAuthorized(placeOrderRequest.getEmail())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Access denied: Not enough rights for this action!"));
        }
        Optional<AppUser> user = userRepo.findByEmail(placeOrderRequest.getEmail());
        if (!user.isPresent()) {
            throw new NoSuchElementException("Error: Email is not presented!");
        }
        Set<SaleProduct> cart = placeOrderRequest.getOrder();
        float cartCost = placeOrderRequest.getTotal();
        Set<Product> rejectedProducts = isCartInStock(cart);
        if (!rejectedProducts.isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(("The number of products in the cart exceeds the " +
                            "available in the shop:\n" + rejectedProducts.stream().map(product ->
                            "'"+product.getDescription()+"' - available amount: "+product.getAmount()+";")
                            .collect(Collectors.joining("\n")))));

        }
        Set<SaleProduct> updatedCart = saveSaleProducts(cart);
        Ordering order = new Ordering(null, user.get().getEmail(), updatedCart, cartCost,true);
        saveOrdering(order);
        log.info(emailService.sendOrderToEmail(order.getCart(),cartCost,user.get().getEmail()));
        log.info("Order processed successfully..");

        OrderResponse orderResponse = new OrderResponse(updatedCart, cartCost, new Date().toString(), order.getId());
        return ResponseEntity.ok(orderResponse);
    }

    public ResponseEntity<?> closeOrder(CloseOrderRequest closeOrderRequest) throws AccessException {
        Optional<Ordering> order = orderingRepo.findById(closeOrderRequest.getOrderId());
        if (!order.isPresent()) {
            throw new NoSuchElementException("Error: Order is not presented!");
        }
        Ordering ordering = order.get();
        if (!isAuthorized(ordering.getUserEmail())){
            throw new AccessException("Access denied: Not enough rights for this action!");
        }
        ordering.setActive(false);
        saveOrdering(ordering);
        log.info("Order closed successfully..");
        return ResponseEntity.ok(new MessageResponse("Order "+ordering.getId().toString()+" closed successfully"));
    }

    public ResponseEntity<?> updateOrder(PlaceOrderRequest placeOrderRequest, Long id) {
        Ordering ordering = getOrderById(id);
        ordering.setCart(placeOrderRequest.getOrder());
        ordering.setTotal(placeOrderRequest.getTotal());
        ordering.setUserEmail(placeOrderRequest.getEmail());
        saveOrdering(ordering);
        return ResponseEntity.ok(new MessageResponse("Order #"+id+" updated successfully"));
    }


    public List<Ordering> getAllOrders() {
        return orderingRepo.findAll();
    }

    public List<Ordering> getAllActiveOrders() {
        return orderingRepo.findAllByActiveIsTrue();
    }

    public List<SaleProduct> getAllSaleProducts() {
        return saleProductRepo.findAll();
    }

    public List<Ordering> getAllOrdersByEmail(String email) {
        return orderingRepo.findAllByUserEmail(email);
    }

    public List<Ordering> getActiveUserOrders(String email) {
        return orderingRepo.findAllByUserEmailAndActiveIsTrue(email);
    }

    public List<Ordering> getClosedUserOrders(String email) {
        return orderingRepo.findAllByUserEmailAndActiveIsFalse(email);
    }

    @Transactional
    public Set<SaleProduct> saveSaleProducts(Set<SaleProduct> cart){
        Set<SaleProduct> updatedCart = new HashSet<>();
        for (SaleProduct saleProduct : cart) {
            Optional<SaleProduct> sp = saleProductRepo.findByProductIdAndAmount(
                    saleProduct.getProductId(),
                    saleProduct.getAmount());
            SaleProduct prod;
            if (!sp.isPresent()) {
                prod = saleProductRepo.save(saleProduct);
                updatedCart.add(prod);
            } else {
                prod = sp.get();
                updatedCart.add(prod);
            }
        }
        return updatedCart;
    }

    @Transactional
    public void saveOrdering(Ordering order){
        orderingRepo.save(order);
    }
}

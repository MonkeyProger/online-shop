package org.scenter.onlineshop.services;

import lombok.AllArgsConstructor;
import org.scenter.onlineshop.domain.Order;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.domain.SaleProduct;
import org.scenter.onlineshop.repo.OrderRepo;
import org.scenter.onlineshop.repo.ProductRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepo orderRepo;
    private ProductRepo productRepo;

    public float getCartAmount(List<SaleProduct> productList){
        float totalSum = 0f;
        float currentSum;
        int newAmount = 0;

        for (SaleProduct cartProduct : productList) {
            Long productId = cartProduct.getProductId();
            Optional<Product> repoProduct = productRepo.findById(productId);
            if (repoProduct.isPresent()){
                Product product = repoProduct.get();
                if (product.getAmount() < cartProduct.getAmount()){
                    currentSum = product.getPrice() * product.getAmount();
                    cartProduct.setAmount(product.getAmount());
                } else {
                    currentSum = product.getPrice() * cartProduct.getAmount();
                    newAmount = product.getAmount() - cartProduct.getAmount();
                }
                totalSum += currentSum;
                product.setAmount(newAmount);
                newAmount = 0;
                cartProduct.setPrice(currentSum);
                productRepo.save(product);
            }
        }
        return totalSum;
    }

    public Order saveOrder(Order order){
        return orderRepo.save(order);
    }
}

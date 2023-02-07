package org.scenter.onlineshop.services;

import lombok.AllArgsConstructor;
import org.scenter.onlineshop.domain.Ordering;
import org.scenter.onlineshop.domain.Product;
import org.scenter.onlineshop.domain.SaleProduct;
import org.scenter.onlineshop.repo.OrderingRepo;
import org.scenter.onlineshop.repo.ProductRepo;
import org.scenter.onlineshop.repo.SaleProductRepo;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderService {

    private OrderingRepo orderingRepo;
    private SaleProductRepo saleProductRepo;
    private ProductRepo productRepo;

    public float getCartCost(List<SaleProduct> productList){
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
                productRepo.save(product);
            }
        }
        return totalSum;
    }

    public List<Ordering> getAllOrders() {return orderingRepo.findAll();}
    public List<Ordering> getAllOrdersByEmail(String email) {return orderingRepo.findAllByUserEmail(email);}
    @Transactional
    public void saveSaleProducts(List<SaleProduct> order){
        saleProductRepo.saveAll(order);
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

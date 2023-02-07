package org.scenter.onlineshop.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.domain.*;
import org.scenter.onlineshop.repo.CategoryRepo;
import org.scenter.onlineshop.repo.ProductRepo;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class StockService {
    private CategoryRepo categoryRepo;
    private ProductRepo productRepo;

    public List<Product> getAllProducts(){
        return productRepo.findAll();
    }
    public Product getProductById(Long productId) {
        Optional<Product> product = productRepo.findById(productId);
        if (product.isEmpty()){
            throw new NoSuchElementException("Product with id " + productId + "not found");
        }
        log.info("Product '{}' found in the database", product.get().getName());
        return product.get();
    }
    public Product getProductByName(String productName) {
        Optional<Product> product = productRepo.findByName(productName);
        if (product.isEmpty()){
            throw new NoSuchElementException("Product with name " + productName + "not found");
        }
        log.info("Product '{}' found in the database", productName);
        return product.get();
    }

    public List<Category> getAllCategories(){
        return categoryRepo.findAll();
    }
    public Category getCategoryById(Long categoryId) {
        Optional<Category> category = categoryRepo.findById(categoryId);
        if (category.isEmpty()){
            throw new NoSuchElementException("Category with id " + categoryId + "not found");
        }
        log.info("Category '{}' found in the database", category.get().getName());
        return category.get();
    }
    public Category getCategoryByName(String categoryName) {
        Optional<Category> category = categoryRepo.findByName(categoryName);
        if (category.isEmpty()){
            throw new NoSuchElementException("Category with name " + categoryName + "not found");
        }
        log.info("Category '{}' found in the database", categoryName);
        return category.get();
    }

    public void saveProductToCategory(String productName, String categoryName){
        Category category = getCategoryByName(categoryName);
        Product product = getProductByName(productName);
        List<Product> oldProducts = category.getProducts();
        if (oldProducts.contains(product)) {
            log.info("Product '{}' is already in category '{}'", productName,categoryName);
            throw new IllegalArgumentException("Product is already in category");
        }
        oldProducts.add(product);
        category.setProducts(oldProducts);
        saveCategory(category);
    }
    public void saveParentToCategory(String child, String parent){
        Category parentCategory = getCategoryByName(parent);
        Category childCategory = getCategoryByName(child);
        childCategory.setParentId(parentCategory.getId());
        saveCategory(childCategory);
    }

    @Transactional
    public void saveProduct(Product product) {productRepo.save(product);}
    @Transactional
    public void saveCategory(Category category) {categoryRepo.save(category);}
}

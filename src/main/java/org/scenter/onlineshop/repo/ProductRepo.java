package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product,Long> {
    Product findByName(String name);
}

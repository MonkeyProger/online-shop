package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product,Long> {
    Optional<Product> findByName(String name);
}

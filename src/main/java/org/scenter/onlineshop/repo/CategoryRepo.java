package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.Category;
import org.scenter.onlineshop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface CategoryRepo extends JpaRepository<Category,Long> {
    Optional<Category> findByName(String name);
    Set<Category> findAllByParentId(Long id);
    Set<Category> findAllByProductsContains(Product product);
}

package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.CharacteristicValue;
import org.scenter.onlineshop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product,Long> {
    Optional<Product> findByTitle(String name);
    List<Product> findAllByCharacteristicValuesContains(CharacteristicValue value);
}

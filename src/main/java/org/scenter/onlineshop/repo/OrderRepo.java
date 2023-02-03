package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.Category;
import org.scenter.onlineshop.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order,Long> {
    @Override
    Optional<Order> findById(Long id);
}

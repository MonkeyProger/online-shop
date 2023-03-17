package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.SaleProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SaleProductRepo extends JpaRepository<SaleProduct,Long> {
    Optional<SaleProduct> findByProductIdAndAmount(Long productId, Integer amount);
}

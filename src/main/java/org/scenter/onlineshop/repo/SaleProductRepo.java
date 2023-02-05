package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.SaleProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleProductRepo extends JpaRepository<SaleProduct,Long> {
}

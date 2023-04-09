package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.ProductFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductFileRepo extends JpaRepository<ProductFile, Long> {
}

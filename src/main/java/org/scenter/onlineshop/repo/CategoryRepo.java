package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category,Long> {
    Category findByName(String name);
}

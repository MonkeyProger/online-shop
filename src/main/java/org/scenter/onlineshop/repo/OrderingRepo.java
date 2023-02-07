package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderingRepo extends JpaRepository<Ordering,Long> {
    Optional<Ordering> findById(Long id);
    List<Ordering> findAllByUserEmail(String email);
}

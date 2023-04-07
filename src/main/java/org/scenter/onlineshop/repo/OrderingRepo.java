package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderingRepo extends JpaRepository<Ordering,Long> {
    List<Ordering> findAllByUserEmail(String email);
    List<Ordering> findAllByActiveIsTrue();

    List<Ordering> findAllByUserEmailAndActiveIsTrue(String email);
    List<Ordering> findAllByUserEmailAndActiveIsFalse(String email);
}

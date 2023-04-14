package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.CharacteristicValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacteristicValueRepo extends JpaRepository<CharacteristicValue, Long> {
    Optional<CharacteristicValue> findByValue(String value);
}

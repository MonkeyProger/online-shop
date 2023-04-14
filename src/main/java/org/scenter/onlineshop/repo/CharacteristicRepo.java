package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.Characteristic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacteristicRepo extends JpaRepository<Characteristic, Long> {
    Optional<Characteristic> findByName(String name);
    Characteristic getCharacteristicByName(String name);
}

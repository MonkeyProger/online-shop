package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.ERole;
import org.scenter.onlineshop.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role,Long> {
    Optional<Role> findByName(ERole name);
}

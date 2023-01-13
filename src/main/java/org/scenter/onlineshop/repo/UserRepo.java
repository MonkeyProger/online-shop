package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<AppUser,Long> {
    AppUser findByEmail(String email);
}

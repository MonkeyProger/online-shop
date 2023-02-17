package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.ResponseFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponseFileRepo extends JpaRepository<ResponseFile, Long> {
}

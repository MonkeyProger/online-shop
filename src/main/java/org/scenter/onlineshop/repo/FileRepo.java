package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepo extends JpaRepository<FileDB, String> {
}

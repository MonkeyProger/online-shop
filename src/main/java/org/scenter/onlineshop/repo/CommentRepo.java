package org.scenter.onlineshop.repo;

import org.scenter.onlineshop.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepo extends JpaRepository<Comment,Long> {
    List<Comment> findAllByUserEmail(String email);

}

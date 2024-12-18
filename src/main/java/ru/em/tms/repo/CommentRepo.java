package ru.em.tms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.em.tms.model.db.Comment;

public interface CommentRepo extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
}

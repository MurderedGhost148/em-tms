package ru.em.tms.lib.specification;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.em.tms.model.db.Comment;
import ru.em.tms.model.db.Task;

@UtilityClass
public class CommentSpecifications {
    public static Specification<Comment> byTask(Task task) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("task"), task);
    }
    public static Specification<Comment> byId(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id);
    }
}

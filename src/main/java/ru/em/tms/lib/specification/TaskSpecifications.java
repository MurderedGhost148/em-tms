package ru.em.tms.lib.specification;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.em.tms.model.db.Task;

@UtilityClass
public class TaskSpecifications {
    public static Specification<Task> byAuthorId(Integer authorId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Task> byExecutorId(Integer executorId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("executor").get("id"), executorId);
    }
}

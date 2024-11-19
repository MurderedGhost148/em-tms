package ru.em.tms.model.db;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment extends AuditEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    @ManyToOne
    private Task task;

    @Builder
    public Comment(LocalDateTime createdAt, LocalDateTime updatedAt, User author, Long id, String content, Task task) {
        super(createdAt, updatedAt, author);
        this.id = id;
        this.content = content;
        this.task = task;
    }
}

package ru.em.tms.model.db;

import jakarta.persistence.*;
import lombok.*;
import ru.em.tms.model.enums.task.Priority;
import ru.em.tms.model.enums.task.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task extends AuditEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    @ManyToOne
    private User executor;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Task(LocalDateTime createdAt, LocalDateTime updatedAt, User author, Long id, String title, String description, Status status, Priority priority, User executor, List<Comment> comments) {
        super(createdAt, updatedAt, author);
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.executor = executor;
        this.comments = comments;
    }
}

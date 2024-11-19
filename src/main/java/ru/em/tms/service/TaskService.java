package ru.em.tms.service;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.em.tms.lib.filter.db.TaskFilter;
import ru.em.tms.lib.mapper.TaskMapper;
import ru.em.tms.lib.specification.TaskSpecifications;
import ru.em.tms.model.db.Task;
import ru.em.tms.model.db.User;
import ru.em.tms.model.dto.PageableResponse;
import ru.em.tms.model.dto.task.TaskCreateDTO;
import ru.em.tms.model.dto.task.TaskGetDTO;
import ru.em.tms.model.dto.task.TaskUpdateDTO;
import ru.em.tms.model.enums.Role;
import ru.em.tms.model.enums.task.Status;
import ru.em.tms.repo.TaskRepo;
import ru.em.tms.repo.UserRepo;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {
    private final TaskRepo repo;
    private final UserRepo userRepo;
    private final TaskMapper mapper;
    private User author;

    @PostConstruct
    public void init() {
        author = userRepo.findAll().stream().filter(role -> role.getRole().equals(Role.ADMIN)).findFirst().orElseThrow();
    }

    @Transactional(readOnly = true)
    public PageableResponse<TaskGetDTO> getAll(Pageable pageable, TaskFilter filter) {
        Specification<Task> spec = Specification.where(null);
        if (filter.getAuthorId() != null) spec = spec.and(TaskSpecifications.byAuthorId(filter.getAuthorId()));
        if (filter.getExecutorId() != null) spec = spec.and(TaskSpecifications.byExecutorId(filter.getExecutorId()));

        var page = repo.findAll(spec, pageable);

        return new PageableResponse<>(page.get()
                .map(mapper::sourceToDestination)
                .toList(),
                page.getTotalPages(),
                page.getPageable().getPageNumber(),
                page.getPageable().getPageSize());
    }

    @Transactional(readOnly = true)
    public Optional<TaskGetDTO> getById(Long id) {
        return repo.findById(id).map(mapper::sourceToDestination);
    }

    public TaskGetDTO create(TaskCreateDTO dto) {
        var task = repo.save(Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .status(Status.NEW)
                .priority(dto.getPriority())
                .author(author)
                .executor(userRepo.findById(dto.getExecutorId()).orElseThrow(() -> new EntityNotFoundException("Исполнитель не найден")))
                .build());

        return mapper.sourceToDestination(task);
    }

    public TaskGetDTO update(Long id, TaskUpdateDTO dto) {
        var saved = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));

        saved.setTitle(dto.getTitle());
        saved.setDescription(dto.getDescription());
        saved.setStatus(dto.getStatus());
        saved.setPriority(dto.getPriority());
        saved.setExecutor(userRepo.findById(dto.getExecutorId()).orElseThrow(() -> new EntityNotFoundException("Исполнитель не найден")));

        return mapper.sourceToDestination(saved);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}

package ru.em.tms.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.em.tms.lib.mapper.CommentMapper;
import ru.em.tms.model.db.Comment;
import ru.em.tms.model.db.Task;
import ru.em.tms.model.dto.PageableResponse;
import ru.em.tms.model.dto.comment.CommentEditDTO;
import ru.em.tms.model.dto.comment.CommentGetDTO;
import ru.em.tms.repo.CommentRepo;
import ru.em.tms.repo.TaskRepo;

import java.util.Optional;

import static ru.em.tms.lib.specification.CommentSpecifications.byId;
import static ru.em.tms.lib.specification.CommentSpecifications.byTask;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepo repo;
    private final TaskRepo taskRepo;
    private final UserService userService;
    private final CommentMapper mapper;

    @Transactional(readOnly = true)
    public PageableResponse<CommentGetDTO> getAll(Long taskId, Pageable pageable) {
        var page = repo.findAll(byTask(getTask(taskId)), pageable);

        return new PageableResponse<>(page.get()
                .map(mapper::sourceToDestination)
                .toList(),
                page.getTotalPages(),
                page.getPageable().getPageNumber(),
                page.getPageable().getPageSize());
    }

    @Transactional(readOnly = true)
    public Optional<CommentGetDTO> getById(Long taskId, Long id) {
        return repo.findOne(byTask(getTask(taskId)).and(byId(id)))
                .map(mapper::sourceToDestination);
    }

    public CommentGetDTO create(Long taskId, CommentEditDTO dto) {
        var comment = repo.save(Comment.builder()
                .task(getTask(taskId))
                .content(dto.getContent())
                .author(userService.getCurrentUser())
                .build());

        return mapper.sourceToDestination(comment);
    }

    public CommentGetDTO update(Long taskId, Long id, CommentEditDTO dto) {
        var saved = repo.findOne(byTask(getTask(taskId)).and(byId(id))).orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));

        saved.setContent(dto.getContent());

        return mapper.sourceToDestination(saved);
    }

    public void delete(Long taskId, Long id) {
        repo.delete(byTask(getTask(taskId)).and(byId(id)));
    }

    private Task getTask(Long taskId) {
        return taskRepo.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));
    }
}

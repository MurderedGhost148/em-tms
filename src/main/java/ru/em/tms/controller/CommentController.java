package ru.em.tms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.em.tms.lib.annotation.PageableDoc;
import ru.em.tms.model.dto.PageableResponse;
import ru.em.tms.model.dto.comment.CommentGetDTO;
import ru.em.tms.model.dto.comment.CommentEditDTO;
import ru.em.tms.service.CommentService;

@RestController
@RequestMapping(value = "/tasks/{taskId}/comments", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Задачи")
public class CommentController {
    private final CommentService service;

    @GetMapping
    @Operation(summary = "Получить список комментариев", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    ))
    @PageableDoc
    @PreAuthorize("isTaskMember(#taskId) or hasAuthority('ADMIN')")
    public PageableResponse<CommentGetDTO> getAll(@PathVariable Long taskId, @ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return service.getAll(taskId, pageable);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Получить информацию о комментарии", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    ))
    @PreAuthorize("isTaskMember(#taskId) or hasAuthority('ADMIN')")
    public CommentGetDTO getById(@PathVariable Long taskId, @PathVariable Long id) {
        return service.getById(taskId, id).orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));
    }

    @PostMapping
    @Operation(summary = "Добавить новый комментарий",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Информация о новой задаче",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentEditDTO.class)))
    )
    @PreAuthorize("isTaskMember(#taskId) or hasAuthority('ADMIN')")
    public CommentGetDTO create(@PathVariable Long taskId, @RequestBody @Validated CommentEditDTO commentDTO) {
        return service.create(taskId, commentDTO);
    }

    @PutMapping(path = "/{id}")
    @Operation(summary = "Изменить комментарий",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Информация о задаче",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentEditDTO.class)))
    )
    @PreAuthorize("isCommentAuthor(#id) or hasAuthority('ADMIN')")
    public CommentGetDTO update(@PathVariable Long taskId, @PathVariable Long id, @RequestBody @Validated CommentEditDTO commentDTO) {
        return service.update(taskId, id, commentDTO);
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Удалить комментарий", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    ))
    @PreAuthorize("isCommentAuthor(#id) or hasAuthority('ADMIN')")
    public void delete(@PathVariable Long taskId, @PathVariable Long id) {
        service.delete(taskId, id);
    }
}

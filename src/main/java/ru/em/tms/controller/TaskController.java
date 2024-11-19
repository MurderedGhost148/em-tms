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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.em.tms.lib.annotation.PageableDoc;
import ru.em.tms.lib.filter.db.TaskFilter;
import ru.em.tms.model.dto.PageableResponse;
import ru.em.tms.model.dto.RestError;
import ru.em.tms.model.dto.task.TaskCreateDTO;
import ru.em.tms.model.dto.task.TaskGetDTO;
import ru.em.tms.model.dto.task.TaskUpdateDTO;
import ru.em.tms.service.TaskService;

@RestController
@RequestMapping(path = "/tasks", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Задачи", description = "Управление задачами и комментариями")
public class TaskController {
    private final TaskService service;

    @GetMapping
    @Operation(summary = "Получить список задач", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)), responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK",
                    content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PageableResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RestError.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RestError.class)))
    })
    @PageableDoc
    public PageableResponse<TaskGetDTO> getAll(@ParameterObject @PageableDefault(size = 50) Pageable pageable,
                                               @ParameterObject TaskFilter filter) {
        return service.getAll(pageable, filter);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Получить информацию о задаче", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)), responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK",
                    content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = PageableResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RestError.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Задача не найдена",
                    content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RestError.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RestError.class)))
    })
    @PreAuthorize("isTaskMember(#id) or hasAuthority('ADMIN')")
    public TaskGetDTO getById(@PathVariable Long id) {
        return service.getById(id).orElseThrow(() -> new EntityNotFoundException("Задача не найдена"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новую задачу",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Информация о новой задаче",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskCreateDTO.class))),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "OK",
                            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TaskGetDTO.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Доступ запрещен",
                            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RestError.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера",
                            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RestError.class)))
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public TaskGetDTO create(@RequestBody @Validated TaskCreateDTO taskDTO) {
        return service.create(taskDTO);
    }

    @PutMapping(path = "/{id}")
    @Operation(summary = "Изменить задачу",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Информация о задаче",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TaskUpdateDTO.class))),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK",
                            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = TaskGetDTO.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Доступ запрещен",
                            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RestError.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера",
                            content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RestError.class)))
    })
    @PreAuthorize("isTaskMember(#id) or hasAuthority('ADMIN')")
    public TaskGetDTO update(@PathVariable Long id, @RequestBody @Validated TaskUpdateDTO taskDTO) {
        return service.update(id, taskDTO);
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Удалить задачу", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)), responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RestError.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Ошибка сервера",
                    content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = RestError.class)))
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

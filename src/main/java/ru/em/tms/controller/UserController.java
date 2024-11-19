package ru.em.tms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.em.tms.lib.annotation.PageableDoc;
import ru.em.tms.model.RestError;
import ru.em.tms.model.dto.PageableResponse;
import ru.em.tms.model.dto.user.UserEditDTO;
import ru.em.tms.model.dto.user.UserGetDTO;
import ru.em.tms.service.UserService;

@RestController
@RequestMapping(path = "/users", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Пользователи")
public class UserController {
    private final UserService service;

    @GetMapping
    @Operation(summary = "Получить список пользователей", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    ))
    @PageableDoc
    public PageableResponse<UserGetDTO> getAll(@ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Получить информацию о пользователе", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    ))
    public UserGetDTO getById(@PathVariable Integer id) {
        return service.getById(id).orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить нового пользователя",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Информация о новом пользователе",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserEditDTO.class)))
    )
    public UserGetDTO create(@RequestBody @Validated UserEditDTO userDTO) {
        return service.create(userDTO);
    }

    @PutMapping(path = "/{id}")
    @Operation(summary = "Изменить пользователя",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Информация о пользователе",
                    required = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserEditDTO.class)))
    )
    public UserGetDTO update(@PathVariable Integer id, @RequestBody @Validated UserEditDTO userDTO) {
        return service.update(id, userDTO);
    }

    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Удалить пользователя", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
    ))
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public RestError handleException() {
        return new RestError("Пользователь с таким email уже существует");
    }
}

package ru.em.tms.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Error")
@Data
@AllArgsConstructor
public class RestError {
    @Schema(description = "Сообщение об ошибке", example = "Неверные данные")
    private String message;
}

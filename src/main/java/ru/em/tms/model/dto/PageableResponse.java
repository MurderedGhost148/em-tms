package ru.em.tms.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.util.List;

@Schema(description = "PageResponse")
@Value
public class PageableResponse<DTO extends IDTO> {
    @Schema(description = "Список", example = "[]")
    List<DTO> result;
    @Schema(description = "Кол-во страниц", example = "2")
    @JsonProperty("total_pages")
    Integer totalPages;
    @Schema(description = "Номер текущей страницы (0..N)", example = "0")
    Integer page;
    @Schema(description = "Кол-во элементов на странице", example = "50")
    Integer size;
}

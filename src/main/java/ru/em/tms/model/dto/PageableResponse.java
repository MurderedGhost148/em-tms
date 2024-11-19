package ru.em.tms.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.util.List;

@Value
public class PageableResponse<DTO extends IDTO> {
    List<DTO> result;
    @JsonProperty("total_pages")
    Integer totalPages;
    Integer page;
    Integer size;
}

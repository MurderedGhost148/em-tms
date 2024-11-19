package ru.em.tms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class JwtDTO implements IDTO {
    String jwt;
}

package ru.em.tms.lib.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Parameter(in = ParameterIn.QUERY
        , description = "Номер страницы (0..N)"
        , name = "page"
        , content = @Content(schema = @Schema(type = "integer", defaultValue = "0")))
@Parameter(in = ParameterIn.QUERY
        , description = "Размер страницы"
        , name = "size"
        , content = @Content(schema = @Schema(type = "integer", defaultValue = "50")))
@Parameter(in = ParameterIn.QUERY
        , description = "Поля, по которым производится сортировка. Формат: property(,asc|desc). "
        , name = "sort"
        , content = @Content(array = @ArraySchema(schema = @Schema(type = "string"))))
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PageableDoc {
}

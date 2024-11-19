package ru.em.tms.lib.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.em.tms.model.db.Task;
import ru.em.tms.model.dto.task.TaskGetDTO;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "executor.id", target = "executorId")
    TaskGetDTO sourceToDestination(Task task);
}

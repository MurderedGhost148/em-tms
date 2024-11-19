package ru.em.tms.lib.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.em.tms.model.db.Comment;
import ru.em.tms.model.dto.comment.CommentGetDTO;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "task.id", target = "taskId")
    CommentGetDTO sourceToDestination(Comment comment);
}

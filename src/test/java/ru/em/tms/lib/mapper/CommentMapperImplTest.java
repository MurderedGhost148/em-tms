package ru.em.tms.lib.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.em.tms.model.db.Comment;
import ru.em.tms.model.db.Task;
import ru.em.tms.model.db.User;
import ru.em.tms.model.dto.comment.CommentGetDTO;

import java.time.LocalDateTime;

class CommentMapperImplTest {
    private final CommentMapperImpl commentMapperImpl = new CommentMapperImpl();

    @Test
    void sourceToDestination_whenFullObject_returnsDTO() {
        var comment = Comment.builder()
                .id(1L).content("test")
                .author(User.builder().id(1).build())
                .task(Task.builder().id(1L).build())
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
        var expected = CommentGetDTO.builder()
                .id(comment.getId()).content(comment.getContent())
                .authorId(comment.getAuthor().getId())
                .taskId(comment.getTask().getId())
                .createdAt(comment.getCreatedAt()).updatedAt(comment.getUpdatedAt())
                .build();

        var actual = commentMapperImpl.sourceToDestination(comment);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void sourceToDestination_whenEmptyObject_returnsDTO() {
        var comment = Comment.builder()
                .id(1L)
                .author(User.builder().id(1).build())
                .task(Task.builder().id(1L).build())
                .build();
        var expected = CommentGetDTO.builder()
                .id(comment.getId())
                .authorId(comment.getAuthor().getId())
                .taskId(comment.getTask().getId())
                .build();

        var actual = commentMapperImpl.sourceToDestination(comment);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void sourceToDestination_whenRelationsIsNull_returnsDTO() {
        var comment = Comment.builder()
                .id(1L)
                .build();
        var expected = CommentGetDTO.builder()
                .id(comment.getId())
                .build();

        var actual = commentMapperImpl.sourceToDestination(comment);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void sourceToDestination_whenNull_returnsNull() {
        var actual = commentMapperImpl.sourceToDestination(null);

        Assertions.assertNull(actual);
    }
}
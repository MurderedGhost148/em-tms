package ru.em.tms.service;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import ru.em.tms.lib.mapper.CommentMapper;
import ru.em.tms.model.db.Comment;
import ru.em.tms.model.db.Task;
import ru.em.tms.model.dto.comment.CommentEditDTO;
import ru.em.tms.model.dto.comment.CommentGetDTO;
import ru.em.tms.repo.CommentRepo;
import ru.em.tms.repo.TaskRepo;

import java.util.LinkedList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepo repo;
    @Mock
    private TaskRepo taskRepo;
    @Mock
    private UserService userService;
    @Mock
    private CommentMapper mapper;
    @InjectMocks
    private CommentService service;

    @Test
    void getAll_whenAccessiblePageableParams_returnsAll() {
        var pageable = PageRequest.of(0, 10);
        var taskId = 1L;
        var comments = new LinkedList<Comment>(){{
            var task = Task.builder().id(taskId).build();
            for(long i = 0; i < 5; i++) add(Comment.builder().id(i).content("test №" + i).task(task).build());

            task = Task.builder().id(taskId + 1).build();
            for(long i = 5; i < 10; i++) add(Comment.builder().id(i).content("test №" + i).task(task).build());
        }};
        var commentsExcepted = comments.stream().filter(c -> c.getTask().getId() == taskId).toList();
        var pageExcepted = new PageImpl<>(commentsExcepted, pageable, commentsExcepted.size());

        when(taskRepo.findById(anyLong())).thenAnswer(invocation ->
                Optional.of(Task.builder().id(invocation.getArgument(0)).build()));
        when(repo.findAll(any(Specification.class), eq(pageable))).thenReturn(pageExcepted);

        var pageActual = service.getAll(taskId, pageable);

        assertAll(
                () -> Assertions.assertThat(pageActual.getTotalPages()).isEqualTo(pageExcepted.getTotalPages()),
                () -> Assertions.assertThat(pageActual.getPage()).isEqualTo(pageExcepted.getPageable().getPageNumber()),
                () -> Assertions.assertThat(pageActual.getSize()).isEqualTo(pageExcepted.getPageable().getPageSize()),
                () -> Assertions.assertThat(pageActual.getResult().size()).isEqualTo(commentsExcepted.size())
        );
    }

    @Test
    void getAll_whenNotAccessiblePageableParams_returnsNone() {
        var pageable = PageRequest.of(1, 10);
        var taskId = 1L;
        var comments = new LinkedList<Comment>();
        var pageExcepted = new PageImpl<>(comments, pageable, 0);

        when(taskRepo.findById(anyLong())).thenAnswer(invocation ->
                Optional.of(Task.builder().id(invocation.getArgument(0)).build()));
        when(repo.findAll(any(Specification.class),eq(pageable))).thenReturn(pageExcepted);

        var pageActual = service.getAll(taskId, pageable);

        assertAll(
                () -> Assertions.assertThat(pageActual.getTotalPages()).isEqualTo(pageExcepted.getTotalPages()),
                () -> Assertions.assertThat(pageActual.getPage()).isEqualTo(pageExcepted.getPageable().getPageNumber()),
                () -> Assertions.assertThat(pageActual.getSize()).isEqualTo(pageExcepted.getPageable().getPageSize()),
                () -> Assertions.assertThat(pageActual.getResult().size()).isEqualTo(pageExcepted.getContent().size())
        );
        verify(repo).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getAll_whenTaskNotExists_throwsException() {
        var pageable = PageRequest.of(0, 10);
        var taskId = 1L;

        when(taskRepo.findById(anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> service.getAll(taskId, pageable));
    }

    @Test
    void getById_whenCommentExists_returnsComment() {
        var commentId = 1L;
        var taskId = 1L;
        var comment = CommentGetDTO.builder().id(commentId).taskId(taskId).build();

        when(repo.findOne(any(Specification.class)))
                .thenReturn(Optional.of(Comment.builder().task(Task.builder().id(taskId).build()).id(commentId).build()));
        when(taskRepo.findById(anyLong())).thenAnswer(invocation ->
                Optional.of(Task.builder().id(invocation.getArgument(0)).build()));
        when(mapper.sourceToDestination(any())).thenAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            return CommentGetDTO.builder().id(c.getId()).taskId(c.getTask().getId()).build();
        });

        var actual = service.getById(taskId, commentId);

        Assertions.assertThat(actual).isPresent();
        Assertions.assertThat(actual.get()).isEqualTo(comment);
    }

    @Test
    void getById_whenCommentNotExists_returnsNone() {
        var commentId = 1L;
        var taskId = 1L;

        when(repo.findOne(any(Specification.class)))
                .thenReturn(Optional.empty());
        when(taskRepo.findById(anyLong())).thenAnswer(invocation ->
                Optional.of(Task.builder().id(invocation.getArgument(0)).build()));

        var actual = service.getById(taskId, commentId);

        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void create_whenCommentValid_returnsComment() {
        var commentId = 1L;
        var taskId = 1L;
        var commentEditDTO = CommentEditDTO.builder().content("test").build();
        var excepted = CommentGetDTO.builder().id(commentId).content(commentEditDTO.getContent()).taskId(taskId).build();

        when(taskRepo.findById(anyLong())).thenAnswer(invocation ->
                Optional.of(Task.builder().id(invocation.getArgument(0)).build()));
        when(mapper.sourceToDestination(any())).thenAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            return CommentGetDTO.builder().id(c.getId()).taskId(c.getTask().getId()).content(c.getContent()).build();
        });
        when(repo.save(any())).thenAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        var actual = service.create(taskId, commentEditDTO);

        Assertions.assertThat(actual).isEqualTo(excepted);
    }

    @Test
    void update_whenCommentExists_returnsComment() {
        var commentId = 1L;
        var taskId = 1L;
        var comment = Comment.builder().content("test").id(commentId)
                .task(Task.builder().id(taskId).build()).id(commentId).build();
        var commentEditDTO = CommentEditDTO.builder().content("test").build();
        var excepted = CommentGetDTO.builder().id(commentId).content(commentEditDTO.getContent()).taskId(taskId).build();

        when(taskRepo.findById(anyLong())).thenAnswer(invocation ->
                Optional.of(Task.builder().id(invocation.getArgument(0)).build()));
        when(repo.findOne(any(Specification.class))).thenReturn(Optional.of(comment));
        when(mapper.sourceToDestination(any())).thenAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            return CommentGetDTO.builder().id(c.getId()).taskId(c.getTask().getId()).content(c.getContent()).build();
        });

        var actual = service.update(taskId, commentId, commentEditDTO);

        Assertions.assertThat(actual).isEqualTo(excepted);
    }

    @Test
    void update_whenCommentNotExists_throwsException() {
        var commentId = 1L;
        var taskId = 1L;
        var commentEditDTO = CommentEditDTO.builder().content("test").build();

        when(taskRepo.findById(anyLong())).thenAnswer(invocation ->
                Optional.of(Task.builder().id(invocation.getArgument(0)).build()));
        when(repo.findOne(any(Specification.class))).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> service.update(taskId, commentId, commentEditDTO));
    }

    @Test
    void delete_whenCommentExists_deletesComment() {
        var commentId = 1L;
        var taskId = 1L;

        when(taskRepo.findById(anyLong())).thenAnswer(invocation ->
                Optional.of(Task.builder().id(invocation.getArgument(0)).build()));

        service.delete(taskId, commentId);

        verify(repo).delete(any(Specification.class));
    }
}
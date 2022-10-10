package ru.otus.service;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.dao.BookDao;
import ru.otus.dao.CommentDao;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.domain.Genre;
import ru.otus.dto.CommentDto;
import ru.otus.exception.BookNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Тест сервиса комментариев к книгам")
@SpringBootTest
public class CommentServiceImplTest {

    private static final Author EXPECTED_AUTHOR = new Author(1L, "Маша Васильева");
    private static final Genre EXPECTED_GENRE = new Genre(1L, "Стихотворение");
    private static final Book EXPECTED_BOOK = new Book(1L, "Короткое стихотворение", EXPECTED_AUTHOR, List.of(EXPECTED_GENRE));
    private static final Comment EXPECTED_COMMENT = new Comment(1L, "Интересно", EXPECTED_BOOK);
    private static final CommentDto EXPECTED_COMMENT_DTO = new CommentDto(EXPECTED_COMMENT);
    private static final Comment EXPECTED_COMMENT_2 = new Comment(2L, "Скучно", EXPECTED_BOOK);
    private static final CommentDto EXPECTED_COMMENT_DTO_2 = new CommentDto(EXPECTED_COMMENT_2);

    @Autowired
    private CommentService commentService;

    @MockBean
    private BookDao bookDao;

    @MockBean
    private CommentDao commentDao;

    // чтобы не поднималась база; используем только сервис
    @Configuration
    @Import(CommentServiceImpl.class)
    static class CommentServiceImplConfiguration {
    }

    @BeforeEach
    public void setUp() {
        when(bookDao.getById(EXPECTED_BOOK.getId())).thenReturn(Optional.of(EXPECTED_BOOK));

        when(commentDao.save(any())).thenReturn(EXPECTED_COMMENT);
        when(commentDao.deleteById(EXPECTED_COMMENT.getId())).thenReturn(true);
        when(commentDao.getById(EXPECTED_COMMENT.getId())).thenReturn(Optional.of(EXPECTED_COMMENT));
        when(commentDao.getByBookId(EXPECTED_BOOK.getId())).thenReturn(List.of(EXPECTED_COMMENT, EXPECTED_COMMENT_2));
    }

    @DisplayName("Добавляет комментарий к книге в БД")
    @Test
    void shouldInsertComment() {
        CommentDto expectedComment = EXPECTED_COMMENT_DTO;

        expectedComment = commentService.insert(expectedComment);

        Optional<CommentDto> actualComment = commentService.getById(expectedComment.getId());
        compareComments(actualComment, expectedComment);
    }

    @DisplayName("Бросает исключение при добавлении комментария к несуществующей книге")
    @Test
    void shouldFailWhenInsertCommentWithNotExistedBook() {
        Comment comment = new Comment(1L, "Интересно", new Book(2L, "Книга"));
        CommentDto commentDto = new CommentDto(comment);

        assertThatThrownBy(() -> commentService.insert(commentDto))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessageContaining("Book 2 not found");
    }

    @DisplayName("Обновляет комментарий к книге в БД")
    @Test
    void shouldUpdateComment() {
        CommentDto initialComment = EXPECTED_COMMENT_DTO;

        Optional<CommentDto> commentBeforeUpdate = commentService.getById(initialComment.getId());
        compareComments(commentBeforeUpdate, initialComment);

        String randomTxt = UUID.randomUUID().toString();

        Book bookNew = new Book(2L, "My book " + randomTxt);
        when(bookDao.getById(bookNew.getId())).thenReturn(Optional.of(bookNew));

        Comment updatedComment = new Comment(initialComment.getId(), "My title " + randomTxt, bookNew);
        CommentDto updatedCommentDto = new CommentDto(updatedComment);
        when(commentDao.getById(initialComment.getId())).thenReturn(Optional.of(updatedComment));

        commentService.update(updatedCommentDto);

        Optional<CommentDto> actualComment = commentService.getById(initialComment.getId());
        compareComments(actualComment, updatedCommentDto);
    }

    @DisplayName("Удаляет комментарий к книге из БД")
    @Test
    void shouldDeleteComment() {
        CommentDto expectedComment = EXPECTED_COMMENT_DTO;

        when(commentDao.getById(expectedComment.getId())).thenReturn(Optional.empty());

        boolean deleted = commentService.deleteById(expectedComment.getId());
        assertThat(deleted).isEqualTo(true);

        Optional<CommentDto> actualComment = commentService.getById(expectedComment.getId());
        assertThat(actualComment).isEmpty();
    }

    @DisplayName("Возвращает ожидаемый комментарий к книге по id")
    @Test
    void shouldReturnExpectedCommentById() {
        CommentDto expectedComment = EXPECTED_COMMENT_DTO;
        Optional<CommentDto> actualComment = commentService.getById(expectedComment.getId());
        compareComments(actualComment, expectedComment);
    }

    @DisplayName("Возвращает ожидаемый список комментариев по id книги")
    @Test
    void shouldReturnExpectedCommentsByBookId() {
        List<CommentDto> expectedComments = List.of(EXPECTED_COMMENT_DTO, EXPECTED_COMMENT_DTO_2);
        List<CommentDto> actualComments = commentService.getByBookId(EXPECTED_BOOK.getId());
        assertThat(actualComments)
            .usingRecursiveFieldByFieldElementComparator(RecursiveComparisonConfiguration.builder().build())
            .containsExactlyInAnyOrderElementsOf(expectedComments);
    }

    private void compareComments(Optional<CommentDto> actualComment, CommentDto expectedComment) {
        assertThat(actualComment)
            .isNotEmpty()
            .get()
            .usingRecursiveComparison()
            .isEqualTo(expectedComment);
    }
}

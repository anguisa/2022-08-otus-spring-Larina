package ru.otus.service;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
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
import static org.mockito.Mockito.*;

@DisplayName("Тест сервиса комментариев к книгам")
@SpringBootTest
public class CommentServiceImplTest {

    private static final Author EXPECTED_AUTHOR = new Author("1", "Маша Васильева");
    private static final Genre EXPECTED_GENRE = new Genre("2", "Стихотворение");
    private static final Book EXPECTED_BOOK = new Book("3", "Короткое стихотворение", EXPECTED_AUTHOR, List.of(EXPECTED_GENRE));
    private static final Comment EXPECTED_COMMENT = new Comment("4", "Интересно");
    private static final CommentDto EXPECTED_COMMENT_DTO = new CommentDto(EXPECTED_COMMENT.getId(), EXPECTED_COMMENT.getText());
    private static final Comment EXPECTED_COMMENT_2 = new Comment("5", "Скучно");
    private static final CommentDto EXPECTED_COMMENT_DTO_2 = new CommentDto(EXPECTED_COMMENT_2.getId(), EXPECTED_COMMENT_2.getText());
    private static final Book EXPECTED_BOOK_WITH_COMMENTS = new Book("6", "Короткое стихотворение", EXPECTED_AUTHOR, List.of(EXPECTED_GENRE), List.of(EXPECTED_COMMENT, EXPECTED_COMMENT_2));

    @Autowired
    private CommentService commentService;

    @MockBean
    private BookDao bookDao;

    @MockBean
    private CommentDao commentDao;

    // чтобы не поднималась база; используем только сервис и конвертеры
    @Configuration
    @Import(CommentServiceImpl.class)
    @ComponentScan("ru.otus.dto.converter")
    static class CommentServiceImplConfiguration {
    }

    @BeforeEach
    public void setUp() {
        when(bookDao.findById(EXPECTED_BOOK.getId())).thenReturn(Optional.of(EXPECTED_BOOK));
        when(bookDao.findById(EXPECTED_BOOK_WITH_COMMENTS.getId())).thenReturn(Optional.of(EXPECTED_BOOK_WITH_COMMENTS));

        when(commentDao.save(new Comment(EXPECTED_COMMENT.getText()))).thenReturn(EXPECTED_COMMENT);
        when(commentDao.findById(EXPECTED_COMMENT.getId())).thenReturn(Optional.of(EXPECTED_COMMENT));
    }

    @DisplayName("Добавляет комментарий к книге в БД")
    @Test
    void shouldInsertComment() {
        CommentDto expectedComment = EXPECTED_COMMENT_DTO;

        CommentDto insertedComment = commentService.insert(new CommentDto(expectedComment.getText()), EXPECTED_BOOK.getId());
        assertThat(insertedComment).usingRecursiveComparison().isEqualTo(expectedComment);

        Optional<CommentDto> actualComment = commentService.findById(expectedComment.getId());
        compareComments(actualComment, expectedComment);

        verify(bookDao, times(1)).save(new Book(EXPECTED_BOOK.getId(), EXPECTED_BOOK.getTitle(), EXPECTED_BOOK.getAuthor(),
            EXPECTED_BOOK.getGenres(), List.of(new Comment(expectedComment.getText()))));
    }

    @DisplayName("Бросает исключение при добавлении комментария к несуществующей книге")
    @Test
    void shouldFailWhenInsertCommentWithNotExistedBook() {
        Comment comment = new Comment("10", "Интересно");
        CommentDto commentDto = new CommentDto(comment.getId(), comment.getText());

        assertThatThrownBy(() -> commentService.insert(commentDto, "22"))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessageContaining("Book 22 not found");
    }

    @DisplayName("Обновляет комментарий к книге в БД")
    @Test
    void shouldUpdateComment() {
        CommentDto initialComment = EXPECTED_COMMENT_DTO;

        Optional<CommentDto> commentBeforeUpdate = commentService.findById(initialComment.getId());
        compareComments(commentBeforeUpdate, initialComment);

        String randomTxt = UUID.randomUUID().toString();
        Comment updatedComment = new Comment(initialComment.getId(), "My title " + randomTxt);
        CommentDto updatedCommentDto = new CommentDto(updatedComment.getId(), updatedComment.getText());
        when(commentDao.findById(updatedComment.getId())).thenReturn(Optional.of(updatedComment));
        when(commentDao.save(updatedComment)).thenReturn(updatedComment);

        commentService.update(updatedCommentDto);

        Optional<CommentDto> actualComment = commentService.findById(initialComment.getId());
        compareComments(actualComment, updatedCommentDto);
    }

    @DisplayName("Удаляет комментарий к книге из БД")
    @Test
    void shouldDeleteComment() {
        CommentDto expectedComment = EXPECTED_COMMENT_DTO;

        when(commentDao.findById(expectedComment.getId())).thenReturn(Optional.empty());

        commentService.deleteByIdAndBookId(expectedComment.getId(), EXPECTED_BOOK.getId());

        Optional<CommentDto> actualComment = commentService.findById(expectedComment.getId());
        assertThat(actualComment).isEmpty();

        verify(commentDao, times(1)).deleteById(expectedComment.getId());
        verify(bookDao, times(1)).deleteCommentByIdAndBookId(expectedComment.getId(), EXPECTED_BOOK.getId());
    }

    @DisplayName("Возвращает ожидаемый комментарий к книге по id")
    @Test
    void shouldReturnExpectedCommentById() {
        CommentDto expectedComment = EXPECTED_COMMENT_DTO;
        Optional<CommentDto> actualComment = commentService.findById(expectedComment.getId());
        compareComments(actualComment, expectedComment);
    }

    @DisplayName("Возвращает ожидаемый список комментариев по id книги")
    @Test
    void shouldReturnExpectedCommentsByBookId() {
        List<CommentDto> expectedComments = List.of(EXPECTED_COMMENT_DTO, EXPECTED_COMMENT_DTO_2);
        List<CommentDto> actualComments = commentService.findByBookId(EXPECTED_BOOK_WITH_COMMENTS.getId());
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

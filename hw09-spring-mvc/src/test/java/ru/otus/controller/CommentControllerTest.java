package ru.otus.controller;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.assertj.core.matcher.AssertionMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.dto.BookShortDto;
import ru.otus.dto.CommentDto;
import ru.otus.service.CommentService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Тест контроллера комментариев")
@WebMvcTest(CommentController.class)
class CommentControllerTest {

    private static final Book EXPECTED_BOOK = new Book(1L, null, null, null, null);
    private static final BookShortDto EXPECTED_BOOK_DTO = new BookShortDto(EXPECTED_BOOK.getId());
    private static final Comment EXPECTED_COMMENT = new Comment(1L, "Интересно", EXPECTED_BOOK);
    private static final CommentDto EXPECTED_COMMENT_DTO = new CommentDto(EXPECTED_COMMENT.getId(), EXPECTED_COMMENT.getText(), EXPECTED_BOOK_DTO);
    private static final Comment EXPECTED_COMMENT_2 = new Comment(2L, "Скучно", EXPECTED_BOOK);
    private static final CommentDto EXPECTED_COMMENT_DTO_2 = new CommentDto(EXPECTED_COMMENT_2.getId(), EXPECTED_COMMENT_2.getText(), EXPECTED_BOOK_DTO);
    private static final String COMMENT_NOT_FOUND = "Comment 888 not found";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    @BeforeEach
    public void setUp() {
        when(commentService.findById(EXPECTED_COMMENT.getId())).thenReturn(Optional.of(EXPECTED_COMMENT_DTO));
        when(commentService.findByBookId(EXPECTED_BOOK.getId())).thenReturn(List.of(EXPECTED_COMMENT_DTO, EXPECTED_COMMENT_DTO_2));
//        when(bookService.findAll()).thenReturn(List.of(EXPECTED_BOOK_DTO, EXPECTED_BOOK_DTO2));
//        when(authorService.findAll()).thenReturn(List.of(EXPECTED_AUTHOR_DTO, EXPECTED_AUTHOR_DTO2));
//        when(genreService.findAll()).thenReturn(List.of(EXPECTED_GENRE_DTO, EXPECTED_GENRE_DTO2));
    }

    @DisplayName("Возвращает ожидаемый список комментариев")
    @Test
    void shouldReturnExpectedComments() throws Exception {
        long bookId = EXPECTED_BOOK.getId();
        List<CommentDto> expected = List.of(EXPECTED_COMMENT_DTO, EXPECTED_COMMENT_DTO_2);

        mvc.perform(get(String.format("/books/%s/comments", bookId)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("comments"))
            .andExpect(model().attribute("comments", new CommentListMatcher(expected)))
            .andExpect(model().attributeExists("bookId"))
            .andExpect(model().attribute("bookId", bookId))
            .andExpect(view().name("list_comments"));
    }

    @DisplayName("Обрабатывает удаление комментария")
    @Test
    void shouldPerformDeleteComment() throws Exception {
        long bookId = EXPECTED_BOOK.getId();
        long commentId = EXPECTED_COMMENT.getId();

        mvc.perform(post(String.format("/books/%s/comments/delete", bookId))
                .param("id", Long.toString(commentId))
                .param("bookId", Long.toString(bookId))
            )
            .andDo(print())
            .andExpect(redirectedUrl(String.format("/books/%s/comments", bookId)));
    }

    @DisplayName("Возвращает ошибку, если комментарий для редактирования не существует")
    @Test
    void shouldReturnErrorIfEditedCommentNotExists() throws Exception {
        long bookId = EXPECTED_BOOK.getId();

        mvc.perform(get(String.format("/books/%s/comments/edit", bookId))
                .queryParam("id", "888")
            )
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string(COMMENT_NOT_FOUND));
    }

    @DisplayName("Возвращает данные для редактирования комментария")
    @Test
    void shouldReturnDataForEditComment() throws Exception {
        long bookId = EXPECTED_BOOK.getId();
        long commentId = EXPECTED_COMMENT.getId();

        mvc.perform(get(String.format("/books/%s/comments/edit", bookId))
                .queryParam("id", Long.toString(commentId))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("comment"))
            .andExpect(model().attribute("comment", new CommentMatcher(EXPECTED_COMMENT_DTO)))
            .andExpect(view().name("edit_comment"));
    }

    @DisplayName("Обрабатывает редактирование комментария")
    @Test
    void shouldPerformEditComment() throws Exception {
        long bookId = EXPECTED_BOOK.getId();

        CommentDto expectedComment = new CommentDto(EXPECTED_COMMENT.getId(), UUID.randomUUID().toString(), EXPECTED_BOOK_DTO);

        mvc.perform(post(String.format("/books/%s/comments/edit", bookId))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", Long.toString(expectedComment.getId()))
                .param("text", expectedComment.getText())
                .param("book", Long.toString(expectedComment.getBook().getId()))
            )
            .andDo(print())
            .andExpect(redirectedUrl(String.format("/books/%s/comments", bookId)));

        verify(commentService).update(argThat(actual -> {
            assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expectedComment);
            return true;
        }));
    }

    @DisplayName("Возвращает данные для создания комментария")
    @Test
    void shouldReturnDataForCreateComment() throws Exception {
        long bookId = EXPECTED_BOOK.getId();

        mvc.perform(get(String.format("/books/%s/comments/create", bookId)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("comment"))
            .andExpect(model().attribute("comment", new CommentMatcher(new CommentDto(null, "", new BookShortDto(bookId)))))
            .andExpect(view().name("edit_comment"));
    }

    @DisplayName("Обрабатывает создание комментария")
    @Test
    void shouldPerformCreateComment() throws Exception {
        long bookId = EXPECTED_BOOK.getId();

        CommentDto expectedComment = new CommentDto(null, UUID.randomUUID().toString(), EXPECTED_BOOK_DTO);

        mvc.perform(post(String.format("/books/%s/comments/create", bookId))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("text", expectedComment.getText())
                .param("book", Long.toString(expectedComment.getBook().getId()))
            )
            .andDo(print())
            .andExpect(redirectedUrl(String.format("/books/%s/comments", bookId)));

        verify(commentService).insert(argThat(actual -> {
            assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expectedComment);
            return true;
        }));
    }

    static class CommentMatcher extends AssertionMatcher<CommentDto> {

        private final CommentDto expected;

        public CommentMatcher(CommentDto expected) {
            this.expected = expected;
        }

        @Override
        public void assertion(CommentDto actual) throws AssertionError {
            assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
        }
    }

    static class CommentListMatcher extends AssertionMatcher<List<CommentDto>> {

        private final List<CommentDto> expected;

        public CommentListMatcher(List<CommentDto> expected) {
            this.expected = expected;
        }

        @Override
        public void assertion(List<CommentDto> actual) throws AssertionError {
            assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator(RecursiveComparisonConfiguration.builder().build())
                .containsExactlyInAnyOrderElementsOf(expected);
        }
    }
/*
    static class AuthorListMatcher extends AssertionMatcher<List<AuthorDto>> {

        private final List<AuthorDto> expected;

        public AuthorListMatcher(List<AuthorDto> expected) {
            this.expected = expected;
        }

        @Override
        public void assertion(List<AuthorDto> actual) throws AssertionError {
            assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator(RecursiveComparisonConfiguration.builder().build())
                .containsExactlyInAnyOrderElementsOf(expected);
        }
    }


    static class GenreListMatcher extends AssertionMatcher<List<GenreDto>> {

        private final List<GenreDto> expected;

        public GenreListMatcher(List<GenreDto> expected) {
            this.expected = expected;
        }

        @Override
        public void assertion(List<GenreDto> actual) throws AssertionError {
            assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator(RecursiveComparisonConfiguration.builder().build())
                .containsExactlyInAnyOrderElementsOf(expected);
        }
    }
*/
}
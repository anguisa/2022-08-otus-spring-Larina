package ru.otus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.otus.exception.CommentNotFoundException;
import ru.otus.service.CommentService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    @BeforeEach
    public void setUp() {
        when(commentService.findById(EXPECTED_COMMENT.getId())).thenReturn(Optional.of(EXPECTED_COMMENT_DTO));
        when(commentService.findByBookId(EXPECTED_BOOK.getId())).thenReturn(List.of(EXPECTED_COMMENT_DTO, EXPECTED_COMMENT_DTO_2));
    }

    @DisplayName("Возвращает ожидаемый список комментариев")
    @Test
    void shouldReturnExpectedComments() throws Exception {
        long bookId = EXPECTED_BOOK.getId();
        List<CommentDto> expected = List.of(EXPECTED_COMMENT_DTO, EXPECTED_COMMENT_DTO_2);

        mvc.perform(get(String.format("/api/books/%s/comments", bookId)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @DisplayName("Возвращает ожидаемый комментарий по id")
    @Test
    void shouldReturnExpectedCommentById() throws Exception {
        long bookId = EXPECTED_BOOK.getId();
        CommentDto expected = EXPECTED_COMMENT_DTO;

        mvc.perform(get(String.format("/api/books/%s/comments/%s", bookId, expected.getId())))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @DisplayName("Возвращает ошибку, если комментарий по id не найден")
    @Test
    void shouldThrowIfCommentNotFoundById() throws Exception {
        long bookId = EXPECTED_BOOK.getId();
        long commentId = 888;

        mvc.perform(get(String.format("/api/books/%s/comments/%s", bookId, commentId)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string(COMMENT_NOT_FOUND));
    }

    @DisplayName("Обрабатывает удаление комментария")
    @Test
    void shouldPerformDeleteComment() throws Exception {
        long bookId = EXPECTED_BOOK.getId();
        long commentId = EXPECTED_COMMENT.getId();

        mvc.perform(delete(String.format("/api/books/%s/comments/%s", bookId, commentId)))
            .andDo(print())
            .andExpect(status().isOk());

        verify(commentService, times(1)).deleteById(commentId);
    }

    @DisplayName("Возвращает ошибку, если комментарий для редактирования не существует")
    @Test
    void shouldReturnErrorIfEditedCommentNotExists() throws Exception {
        long bookId = EXPECTED_BOOK.getId();
        long commentId = 888;
        when(commentService.update(any())).thenThrow(new CommentNotFoundException(commentId));

        CommentDto commentToSend = new CommentDto(EXPECTED_COMMENT.getId(), UUID.randomUUID().toString(), EXPECTED_BOOK_DTO);

        mvc.perform(put(String.format("/api/books/%s/comments/%s", bookId, commentToSend.getId()))
                .content(mapper.writeValueAsString(commentToSend))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string(COMMENT_NOT_FOUND));
    }

    @DisplayName("Обрабатывает редактирование комментария")
    @Test
    void shouldPerformEditComment() throws Exception {
        long bookId = EXPECTED_BOOK.getId();

        CommentDto commentToSend = new CommentDto(EXPECTED_COMMENT.getId(), UUID.randomUUID().toString(), EXPECTED_BOOK_DTO);
        CommentDto commentExpected = new CommentDto(commentToSend.getId(), commentToSend.getText(), EXPECTED_BOOK_DTO);

        when(commentService.update(any())).thenReturn(commentExpected);

        mvc.perform(put(String.format("/api/books/%s/comments/%s", bookId, commentToSend.getId()))
                .content(mapper.writeValueAsString(commentToSend))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(commentExpected)));

        verify(commentService).update(argThat(actual -> {
            assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(commentToSend);
            return true;
        }));
    }

    @DisplayName("Обрабатывает создание комментария")
    @Test
    void shouldPerformCreateComment() throws Exception {
        long bookId = EXPECTED_BOOK.getId();

        CommentDto commentToSend = new CommentDto(null, UUID.randomUUID().toString(), EXPECTED_BOOK_DTO);
        CommentDto commentExpected = new CommentDto(10000L, commentToSend.getText(), EXPECTED_BOOK_DTO);

        when(commentService.insert(any())).thenReturn(commentExpected);

        mvc.perform(post(String.format("/api/books/%s/comments", bookId))
                .content(mapper.writeValueAsString(commentToSend))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(commentExpected)));

        verify(commentService).insert(argThat(actual -> {
            assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(commentToSend);
            return true;
        }));
    }

}
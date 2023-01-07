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
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Genre;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.GenreDto;
import ru.otus.exception.BookNotFoundException;
import ru.otus.service.BookService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Тест контроллера книг")
@WebMvcTest(BookController.class)
class BookControllerTest {

    private static final Author EXPECTED_AUTHOR = new Author(1L, "Маша Васильева");
    private static final Author EXPECTED_AUTHOR2 = new Author(2L, "Катя Петрова");
    private static final AuthorDto EXPECTED_AUTHOR_DTO = new AuthorDto(EXPECTED_AUTHOR.getId(), EXPECTED_AUTHOR.getName());
    private static final AuthorDto EXPECTED_AUTHOR_DTO2 = new AuthorDto(EXPECTED_AUTHOR2.getId(), EXPECTED_AUTHOR2.getName());
    private static final Genre EXPECTED_GENRE = new Genre(1L, "Стихотворение");
    private static final Genre EXPECTED_GENRE2 = new Genre(2L, "Проза");
    private static final GenreDto EXPECTED_GENRE_DTO = new GenreDto(EXPECTED_GENRE.getId(), EXPECTED_GENRE.getTitle());
    private static final GenreDto EXPECTED_GENRE_DTO2 = new GenreDto(EXPECTED_GENRE2.getId(), EXPECTED_GENRE2.getTitle());
    private static final Book EXPECTED_BOOK = new Book(1L, "Короткое стихотворение", EXPECTED_AUTHOR, List.of(EXPECTED_GENRE), null);
    private static final Book EXPECTED_BOOK2 = new Book(2L, "Длинная проза", EXPECTED_AUTHOR2, List.of(EXPECTED_GENRE2), null);
    private static final BookDto EXPECTED_BOOK_DTO = new BookDto(EXPECTED_BOOK.getId(), EXPECTED_BOOK.getTitle(), EXPECTED_AUTHOR_DTO, List.of(EXPECTED_GENRE_DTO));
    private static final BookDto EXPECTED_BOOK_DTO2 = new BookDto(EXPECTED_BOOK2.getId(), EXPECTED_BOOK2.getTitle(), EXPECTED_AUTHOR_DTO2, List.of(EXPECTED_GENRE_DTO2));
    private static final String BOOK_NOT_FOUND = "Book 888 not found";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        when(bookService.findById(EXPECTED_BOOK.getId())).thenReturn(Optional.of(EXPECTED_BOOK_DTO));
        when(bookService.findAll()).thenReturn(List.of(EXPECTED_BOOK_DTO, EXPECTED_BOOK_DTO2));
    }

    @DisplayName("Возвращает ожидаемый список книг")
    @Test
    void shouldReturnExpectedBooks() throws Exception {
        List<BookDto> expected = List.of(EXPECTED_BOOK_DTO, EXPECTED_BOOK_DTO2);

        mvc.perform(get("/api/books"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @DisplayName("Возвращает ожидаемую книгу по id")
    @Test
    void shouldReturnExpectedBookById() throws Exception {
        BookDto expected = EXPECTED_BOOK_DTO;

        mvc.perform(get(String.format("/api/books/%s", expected.getId())))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @DisplayName("Возвращает ошибку, если книга по id не найдена")
    @Test
    void shouldThrowIfBookNotFoundById() throws Exception {
        long id = 888;

        mvc.perform(get(String.format("/api/books/%s", id)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string(BOOK_NOT_FOUND));
    }

    @DisplayName("Обрабатывает удаление книги")
    @Test
    void shouldPerformDeleteBook() throws Exception {
        long expectedBookId = EXPECTED_BOOK_DTO.getId();

        mvc.perform(delete(String.format("/api/books/%s", expectedBookId)))
            .andDo(print())
            .andExpect(status().isOk());

        verify(bookService, times(1)).deleteById(expectedBookId);
    }

    @DisplayName("Возвращает ошибку, если книга для редактирования не существует")
    @Test
    void shouldReturnErrorIfEditedBookNotExists() throws Exception {
        long id = 888;
        when(bookService.update(any())).thenThrow(new BookNotFoundException(id));
        BookDto bookToSend = new BookDto(id, "TEST", null, List.of());

        mvc.perform(put(String.format("/api/books/%s", id))
                .content(mapper.writeValueAsString(bookToSend))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string(BOOK_NOT_FOUND));
    }

    @DisplayName("Обрабатывает редактирование книги")
    @Test
    void shouldPerformEditBook() throws Exception {
        BookDto bookToSend = new BookDto(EXPECTED_BOOK.getId(), EXPECTED_BOOK.getTitle(), new AuthorDto(EXPECTED_AUTHOR_DTO.getId(), null),
            List.of(new GenreDto(EXPECTED_GENRE_DTO.getId(), null), new GenreDto(EXPECTED_GENRE_DTO2.getId(), null)));
        BookDto bookToReceive = new BookDto(EXPECTED_BOOK.getId(), EXPECTED_BOOK.getTitle(), EXPECTED_AUTHOR_DTO,
            List.of(EXPECTED_GENRE_DTO, EXPECTED_GENRE_DTO2));
        when(bookService.update(any())).thenReturn(bookToReceive);

        mvc.perform(put(String.format("/api/books/%s", bookToSend.getId()))
                .content(mapper.writeValueAsString(bookToSend))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(bookToReceive)));

        verify(bookService).update(argThat(actual -> {
            assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(bookToSend);
            return true;
        }));
    }

    @DisplayName("Обрабатывает создание книги")
    @Test
    void shouldPerformCreateBook() throws Exception {
        BookDto bookToSend = new BookDto(null, EXPECTED_BOOK.getTitle(), new AuthorDto(EXPECTED_AUTHOR_DTO.getId(), null),
            List.of(new GenreDto(EXPECTED_GENRE_DTO.getId(), null), new GenreDto(EXPECTED_GENRE_DTO2.getId(), null)));
        BookDto bookExpected = new BookDto(1000L, EXPECTED_BOOK.getTitle(), EXPECTED_AUTHOR_DTO,
            List.of(EXPECTED_GENRE_DTO, EXPECTED_GENRE_DTO2));
        when(bookService.insert(any())).thenReturn(bookExpected);

        mvc.perform(post("/api/books")
                .content(mapper.writeValueAsString(bookToSend))
                .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().json(mapper.writeValueAsString(bookExpected)));

        verify(bookService).insert(argThat(actual -> {
            assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(bookToSend);
            return true;
        }));
    }

}
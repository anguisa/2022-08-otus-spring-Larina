package ru.otus.controller;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.assertj.core.matcher.AssertionMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.config.SecurityConfig;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Genre;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.GenreDto;
import ru.otus.service.AuthorService;
import ru.otus.service.BookService;
import ru.otus.service.GenreService;
import ru.otus.service.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Тест контроллера книг")
@WebMvcTest(BookController.class)
@Import(SecurityConfig.class) // WebMvcTest не импортирует по умолчанию
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

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {
        when(bookService.findById(EXPECTED_BOOK.getId())).thenReturn(Optional.of(EXPECTED_BOOK_DTO));
        when(bookService.findAll()).thenReturn(List.of(EXPECTED_BOOK_DTO, EXPECTED_BOOK_DTO2));
        when(authorService.findAll()).thenReturn(List.of(EXPECTED_AUTHOR_DTO, EXPECTED_AUTHOR_DTO2));
        when(genreService.findAll()).thenReturn(List.of(EXPECTED_GENRE_DTO, EXPECTED_GENRE_DTO2));
    }

    @DisplayName("Возвращает ожидаемый список книг")
    @WithMockUser(username = "admin", roles = "USER")
    @Test
    void shouldReturnExpectedBooks() throws Exception {
        List<BookDto> expected = List.of(EXPECTED_BOOK_DTO, EXPECTED_BOOK_DTO2);

        mvc.perform(get("/books"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("books"))
            .andExpect(model().attribute("books", new BookListMatcher(expected)))
            .andExpect(view().name("list_books"));
    }

    @DisplayName("Обрабатывает удаление книги")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void shouldPerformDeleteBook() throws Exception {
        long expectedBookId = EXPECTED_BOOK_DTO.getId();

        mvc.perform(post("/books/delete")
                .param("id", Long.toString(expectedBookId))
            )
            .andDo(print())
            .andExpect(redirectedUrl("/books"));
    }

    @DisplayName("Возвращает ошибку, если книга для редактирования не существует")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void shouldReturnErrorIfEditedBookNotExists() throws Exception {
        mvc.perform(get("/books/edit")
                .queryParam("id", "888")
            )
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string(BOOK_NOT_FOUND));
    }

    @DisplayName("Возвращает данные для редактирования книги")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void shouldReturnDataForEditBook() throws Exception {
        long expectedBookId = EXPECTED_BOOK_DTO.getId();

        mvc.perform(get("/books/edit")
                .queryParam("id", Long.toString(expectedBookId))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("book"))
            .andExpect(model().attribute("book", new BookMatcher(EXPECTED_BOOK_DTO)))
            .andExpect(model().attributeExists("authors"))
            .andExpect(model().attribute("authors", new AuthorListMatcher(List.of(EXPECTED_AUTHOR_DTO, EXPECTED_AUTHOR_DTO2))))
            .andExpect(model().attributeExists("genres"))
            .andExpect(model().attribute("genres", new GenreListMatcher(List.of(EXPECTED_GENRE_DTO, EXPECTED_GENRE_DTO2))))
            .andExpect(view().name("edit_book"));
    }

    @DisplayName("Обрабатывает редактирование книги")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void shouldPerformEditBook() throws Exception {
        BookDto expectedBook = new BookDto(EXPECTED_BOOK.getId(), EXPECTED_BOOK.getTitle(), new AuthorDto(EXPECTED_AUTHOR_DTO.getId()),
            List.of(new GenreDto(EXPECTED_GENRE_DTO.getId()), new GenreDto(EXPECTED_GENRE_DTO2.getId())));

        mvc.perform(post("/books/edit")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("id", Long.toString(expectedBook.getId()))
                .param("title", expectedBook.getTitle())
                .param("author", Long.toString(expectedBook.getAuthor().getId()))
                .param("genres", Long.toString(expectedBook.getGenres().get(0).getId()))
                .param("genres", Long.toString(expectedBook.getGenres().get(1).getId()))
            )
            .andDo(print())
            .andExpect(redirectedUrl("/books"));

        verify(bookService).update(argThat(actual -> {
            assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
            return true;
        }));
    }

    @DisplayName("Возвращает данные для создания книги")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void shouldReturnDataForCreateBook() throws Exception {
        mvc.perform(get("/books/create"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("book"))
            .andExpect(model().attribute("book", new BookMatcher(new BookDto(null, "", new AuthorDto(null), List.of()))))
            .andExpect(model().attributeExists("authors"))
            .andExpect(model().attribute("authors", new AuthorListMatcher(List.of(EXPECTED_AUTHOR_DTO, EXPECTED_AUTHOR_DTO2))))
            .andExpect(model().attributeExists("genres"))
            .andExpect(model().attribute("genres", new GenreListMatcher(List.of(EXPECTED_GENRE_DTO, EXPECTED_GENRE_DTO2))))
            .andExpect(view().name("edit_book"));
    }

    @DisplayName("Обрабатывает создание книги")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void shouldPerformCreateBook() throws Exception {
        BookDto expectedBook = new BookDto(null, EXPECTED_BOOK.getTitle(), new AuthorDto(EXPECTED_AUTHOR_DTO.getId()),
            List.of(new GenreDto(EXPECTED_GENRE_DTO.getId()), new GenreDto(EXPECTED_GENRE_DTO2.getId())));

        mvc.perform(post("/books/create")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("title", expectedBook.getTitle())
                .param("author", Long.toString(expectedBook.getAuthor().getId()))
                .param("genres", Long.toString(expectedBook.getGenres().get(0).getId()))
                .param("genres", Long.toString(expectedBook.getGenres().get(1).getId()))
            )
            .andDo(print())
            .andExpect(redirectedUrl("/books"));

        verify(bookService).insert(argThat(actual -> {
            assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expectedBook);
            return true;
        }));
    }

    @DisplayName("Запрещает доступ для юзера к редактированию книг")
    @WithMockUser(username = "user", authorities = {"USER"})
    @ParameterizedTest
    @CsvSource({"/books/edit,GET", "/books/create,GET", "/books/delete,POST", "/books/edit,POST", "/books/create,POST"})
    void shouldForbidEditBookPagesForUser(String url, String method) throws Exception {
        mvc.perform(MockMvcRequestBuilders.request(HttpMethod.resolve(method), url)).andExpect(status().isForbidden());
    }

    static class BookMatcher extends AssertionMatcher<BookDto> {

        private final BookDto expected;

        public BookMatcher(BookDto expected) {
            this.expected = expected;
        }

        @Override
        public void assertion(BookDto actual) throws AssertionError {
            assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
        }
    }

    static class BookListMatcher extends AssertionMatcher<List<BookDto>> {

        private final List<BookDto> expected;

        public BookListMatcher(List<BookDto> expected) {
            this.expected = expected;
        }

        @Override
        public void assertion(List<BookDto> actual) throws AssertionError {
            assertThat(actual)
                .usingRecursiveFieldByFieldElementComparator(RecursiveComparisonConfiguration.builder().build())
                .containsExactlyInAnyOrderElementsOf(expected);
        }
    }

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

}
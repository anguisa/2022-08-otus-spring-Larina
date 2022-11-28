package ru.otus.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.dao.BookDao;
import ru.otus.dao.CommentDao;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.domain.Genre;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.GenreDto;
import ru.otus.dto.converter.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("Тест контроллера книг")
@WebFluxTest(controllers = BookController.class)
@Import({BookDtoConverter.class, AuthorDtoConverter.class, CommentDtoConverter.class, GenreDtoConverter.class})
@TestPropertySource(properties = "mongock.enabled=false")
class BookControllerTest {

    private static final Author EXPECTED_AUTHOR = new Author("1", "Маша Васильева");
    private static final Author EXPECTED_AUTHOR2 = new Author("2", "Катя Петрова");
    private static final AuthorDto EXPECTED_AUTHOR_DTO = new AuthorDto(EXPECTED_AUTHOR.getId(), EXPECTED_AUTHOR.getName());
    private static final AuthorDto EXPECTED_AUTHOR_DTO2 = new AuthorDto(EXPECTED_AUTHOR2.getId(), EXPECTED_AUTHOR2.getName());
    private static final Genre EXPECTED_GENRE = new Genre("3", "Стихотворение");
    private static final Genre EXPECTED_GENRE2 = new Genre("4", "Проза");
    private static final GenreDto EXPECTED_GENRE_DTO = new GenreDto(EXPECTED_GENRE.getId(), EXPECTED_GENRE.getTitle());
    private static final GenreDto EXPECTED_GENRE_DTO2 = new GenreDto(EXPECTED_GENRE2.getId(), EXPECTED_GENRE2.getTitle());
    private static final Book EXPECTED_BOOK = new Book("5", "Короткое стихотворение", EXPECTED_AUTHOR,
        List.of(EXPECTED_GENRE), List.of(new Comment("6", "Комментарий1"), new Comment("7", "Комментарий2")));
    private static final Book EXPECTED_BOOK2 = new Book("8", "Длинная проза", EXPECTED_AUTHOR2,
        List.of(EXPECTED_GENRE2), List.of(new Comment("9", "Комментарий3"), new Comment("10", "Комментарий4")));
    private static final BookDto EXPECTED_BOOK_DTO = new BookDto(EXPECTED_BOOK.getId(), EXPECTED_BOOK.getTitle(), EXPECTED_AUTHOR_DTO, List.of(EXPECTED_GENRE_DTO));
    private static final BookDto EXPECTED_BOOK_DTO2 = new BookDto(EXPECTED_BOOK2.getId(), EXPECTED_BOOK2.getTitle(), EXPECTED_AUTHOR_DTO2, List.of(EXPECTED_GENRE_DTO2));

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private BookDao bookDao;

    @MockBean
    private CommentDao commentDao;

    @Autowired
    private DtoConverter<Book, BookDto> bookConverter;

    @BeforeEach
    public void setUp() {
        when(bookDao.findById(EXPECTED_BOOK.getId())).thenReturn(Mono.just(EXPECTED_BOOK));
        when(bookDao.deleteById(EXPECTED_BOOK.getId())).thenReturn(Mono.empty());
        when(commentDao.deleteAllById(anyList())).thenReturn(Mono.empty());
        when(bookDao.findAll()).thenReturn(Flux.just(EXPECTED_BOOK, EXPECTED_BOOK2));
    }

    @DisplayName("Возвращает ожидаемый список книг")
    @Test
    void shouldReturnExpectedBooks() {
        List<BookDto> expected = List.of(EXPECTED_BOOK_DTO, EXPECTED_BOOK_DTO2);

        webClient.get()
            .uri("/api/books")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(BookDto.class)
            .hasSize(expected.size())
            .value(actual -> {
                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
            });
    }

    @DisplayName("Возвращает ожидаемую книгу по id")
    @Test
    void shouldReturnExpectedBookById() {
        BookDto expected = EXPECTED_BOOK_DTO;

        webClient.get()
            .uri(String.format("/api/books/%s", expected.getId()))
            .exchange()
            .expectStatus().isOk()
            .expectBody(BookDto.class)
            .value(actual -> {
                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
            });
    }

    @DisplayName("Возвращает ошибку, если книга по id не найдена")
    @Test
    void shouldThrowIfBookNotFoundById() {
        when(bookDao.findById("888")).thenReturn(Mono.empty());

        webClient.get()
            .uri("/api/books/888")
            .exchange()
            .expectStatus().isNotFound();
    }

    @DisplayName("Обрабатывает удаление книги")
    @Test
    void shouldPerformDeleteBook() {
        String expectedBookId = EXPECTED_BOOK_DTO.getId();

        webClient.delete()
            .uri(String.format("/api/books/%s", expectedBookId))
            .exchange()
            .expectStatus().isOk();

        verify(bookDao, times(1)).deleteById(eq(expectedBookId));
        verify(commentDao, times(1)).deleteAllById(eq(EXPECTED_BOOK.getComments().stream().map(Comment::getId).collect(Collectors.toList())));
    }

    @DisplayName("Обрабатывает редактирование книги")
    @Test
    void shouldPerformEditBook() {
        Book bookToSend = new Book(EXPECTED_BOOK.getId(), EXPECTED_BOOK.getTitle(), new Author(EXPECTED_AUTHOR.getId(), null),
            List.of(new Genre(EXPECTED_GENRE.getId(), null), new Genre(EXPECTED_GENRE2.getId(), null)), null);
        BookDto bookToSendDto = new BookDto(EXPECTED_BOOK.getId(), EXPECTED_BOOK.getTitle(), new AuthorDto(EXPECTED_AUTHOR_DTO.getId(), null),
            List.of(new GenreDto(EXPECTED_GENRE_DTO.getId(), null), new GenreDto(EXPECTED_GENRE_DTO2.getId(), null)));
        Book bookToReceive = new Book(EXPECTED_BOOK.getId(), EXPECTED_BOOK.getTitle(), EXPECTED_AUTHOR, List.of(EXPECTED_GENRE, EXPECTED_GENRE2), null);
        BookDto bookToReceiveDto = new BookDto(EXPECTED_BOOK.getId(), EXPECTED_BOOK.getTitle(), EXPECTED_AUTHOR_DTO, List.of(EXPECTED_GENRE_DTO, EXPECTED_GENRE_DTO2));
        when(bookDao.updateBookWithoutComments(any())).thenReturn(Mono.just(bookToReceive));

        webClient.put()
            .uri(String.format("/api/books/%s", bookToSendDto.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(bookToSendDto))
            .exchange()
            .expectStatus().isOk()
            .expectBody(BookDto.class)
            .value(actual -> {
                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(bookToReceiveDto);
            });

        verify(bookDao).updateBookWithoutComments(argThat(actual -> {
            assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(bookToSend);
            return true;
        }));
    }

    @DisplayName("Обрабатывает создание книги")
    @Test
    void shouldPerformCreateBook() {
        Book bookToSend = new Book(null, EXPECTED_BOOK.getTitle(), new Author(EXPECTED_AUTHOR.getId(), null),
            List.of(new Genre(EXPECTED_GENRE.getId(), null), new Genre(EXPECTED_GENRE2.getId(), null)), null);
        BookDto bookToSendDto = new BookDto(null, EXPECTED_BOOK.getTitle(), new AuthorDto(EXPECTED_AUTHOR_DTO.getId(), null),
            List.of(new GenreDto(EXPECTED_GENRE_DTO.getId(), null), new GenreDto(EXPECTED_GENRE_DTO2.getId(), null)));
        Book bookToReceive = new Book("1000", EXPECTED_BOOK.getTitle(), EXPECTED_AUTHOR, List.of(EXPECTED_GENRE, EXPECTED_GENRE2), null);
        BookDto bookToReceiveDto = new BookDto("1000", EXPECTED_BOOK.getTitle(), EXPECTED_AUTHOR_DTO, List.of(EXPECTED_GENRE_DTO, EXPECTED_GENRE_DTO2));
        when(bookDao.save(any())).thenReturn(Mono.just(bookToReceive));

        webClient.post()
            .uri("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(bookToSendDto))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(BookDto.class)
            .value(actual -> {
                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(bookToReceiveDto);
            });

        verify(bookDao).save(argThat(actual -> {
            assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(bookToSend);
            return true;
        }));
    }

}
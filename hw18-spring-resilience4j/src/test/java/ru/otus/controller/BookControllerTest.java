package ru.otus.controller;

import com.mongodb.MongoException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
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
import ru.otus.dto.converter.DtoConverter;
import ru.otus.exception.BookNotFoundException;
import ru.otus.service.BookService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.when;

// https://github.com/resilience4j/resilience4j-spring-boot2-demo
@DisplayName("Тест контроллера книг")
@SpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureMetrics
@TestPropertySource(properties = "mongock.enabled=false")
class BookControllerTest {

    private static final String MONGODB = "mongoDb";
    private static final String FAILED_WITH_RETRY = "failed_with_retry";
    private static final String SUCCESS_WITHOUT_RETRY = "successful_without_retry";

    private static final Author EXPECTED_AUTHOR = new Author("1", "Маша Васильева");
    private static final Author EXPECTED_AUTHOR2 = new Author("2", "Катя Петрова");
    private static final AuthorDto EXPECTED_AUTHOR_DTO = new AuthorDto(EXPECTED_AUTHOR.getId(), EXPECTED_AUTHOR.getName());
    private static final AuthorDto EXPECTED_AUTHOR_DTO2 = new AuthorDto(EXPECTED_AUTHOR2.getId(), EXPECTED_AUTHOR2.getName());
    private static final AuthorDto FALLBACK_AUTHOR_DTO = new AuthorDto("00", "Без имени");
    private static final Genre EXPECTED_GENRE = new Genre("3", "Стихотворение");
    private static final Genre EXPECTED_GENRE2 = new Genre("4", "Проза");
    private static final GenreDto EXPECTED_GENRE_DTO = new GenreDto(EXPECTED_GENRE.getId(), EXPECTED_GENRE.getTitle());
    private static final GenreDto EXPECTED_GENRE_DTO2 = new GenreDto(EXPECTED_GENRE2.getId(), EXPECTED_GENRE2.getTitle());
    private static final GenreDto FALLBACK_GENRE_DTO = new GenreDto("000", "Без названия");
    private static final Book EXPECTED_BOOK = new Book("5", "Короткое стихотворение", EXPECTED_AUTHOR,
        List.of(EXPECTED_GENRE), List.of(new Comment("6", "Комментарий1"), new Comment("7", "Комментарий2")));
    private static final Book EXPECTED_BOOK2 = new Book("8", "Длинная проза", EXPECTED_AUTHOR2,
        List.of(EXPECTED_GENRE2), List.of(new Comment("9", "Комментарий3"), new Comment("10", "Комментарий4")));
    private static final BookDto EXPECTED_BOOK_DTO = new BookDto(EXPECTED_BOOK.getId(), EXPECTED_BOOK.getTitle(), EXPECTED_AUTHOR_DTO, List.of(EXPECTED_GENRE_DTO));
    private static final BookDto EXPECTED_BOOK_DTO2 = new BookDto(EXPECTED_BOOK2.getId(), EXPECTED_BOOK2.getTitle(), EXPECTED_AUTHOR_DTO2, List.of(EXPECTED_GENRE_DTO2));
    private static final BookDto FALLBACK_BOOK_DTO = new BookDto("0", "Без названия", FALLBACK_AUTHOR_DTO, List.of(FALLBACK_GENRE_DTO));

    @MockBean
    private BookDao bookDao;

    @MockBean
    private CommentDao commentDao;

    @Autowired
    private DtoConverter<Book, BookDto> bookConverter;

    @Autowired
    private BookController bookController;

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private BookService bookService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private RetryRegistry retryRegistry;

    @BeforeEach
    public void setUp() {
        when(bookDao.findById(EXPECTED_BOOK.getId())).thenReturn(Mono.just(EXPECTED_BOOK));
        when(bookDao.deleteById(EXPECTED_BOOK.getId())).thenReturn(Mono.empty());
        when(commentDao.deleteAllById(anyList())).thenReturn(Mono.empty());
        when(bookDao.findAll()).thenReturn(Flux.just(EXPECTED_BOOK, EXPECTED_BOOK2));
    }

    @DisplayName("Возвращает данные из фолбэка при открытом circuitBreaker")
    @Test
    void shouldFallbackWhenOpenedCircuitBreaker() {
        circuitBreakerRegistry.circuitBreaker(MONGODB).transitionToOpenState();

        checkHealthStatus(MONGODB, CircuitBreaker.State.OPEN);

        webClient.get()
            .uri("/api/books")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(BookDto.class)
            .hasSize(1)
            .value(actual -> {
                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(FALLBACK_BOOK_DTO));
            });

    }

    @DisplayName("Возвращает корректные данные при закрытом circuitBreaker")
    @Test
    void shouldReturnCorrectDataWhenClosedCircuitBreaker() {
        circuitBreakerRegistry.circuitBreaker(MONGODB).transitionToClosedState();

        checkHealthStatus(MONGODB, CircuitBreaker.State.CLOSED);

        webClient.get()
            .uri("/api/books")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(BookDto.class)
            .hasSize(2)
            .value(actual -> {
                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(EXPECTED_BOOK_DTO, EXPECTED_BOOK_DTO2));
            });

    }

    @DisplayName("Повторяет трижды при выбросе MongoException")
    @Test
    void shouldRetryThreeTimesIfMongoException() {
        long failedWithRetryCount = getCurrentCount(FAILED_WITH_RETRY, MONGODB);
        circuitBreakerRegistry.circuitBreaker(MONGODB).transitionToClosedState();

        AtomicInteger retried = new AtomicInteger();
        when(bookDao.findAll()).thenReturn(
            Mono.<Book>fromCallable(() -> {
                retried.incrementAndGet();
                throw new MongoException("TEST");
            }).flux()
        );

        webClient.get()
            .uri("/api/books")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(BookDto.class)
            .hasSize(1)
            .value(actual -> {
                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(FALLBACK_BOOK_DTO));
            });

        assertThat(getCurrentCount(FAILED_WITH_RETRY, MONGODB)).isEqualTo(failedWithRetryCount + 1);

        assertThat(retried.get()).isEqualTo(3);
    }

    @DisplayName("Не повторяет при выбросе BookNotFoundException")
    @Test
    void shouldNotRetryIfBookNotFoundException() {
        long failedWithRetryCount = getCurrentCount(FAILED_WITH_RETRY, MONGODB);
        circuitBreakerRegistry.circuitBreaker(MONGODB).transitionToClosedState();

        AtomicInteger retried = new AtomicInteger();
        when(bookDao.findAll()).thenReturn(
            Mono.<Book>fromCallable(() -> {
                retried.incrementAndGet();
                throw new BookNotFoundException("TEST");
            }).flux()
        );

        webClient.get()
            .uri("/api/books")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(BookDto.class)
            .hasSize(1)
            .value(actual -> {
                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(FALLBACK_BOOK_DTO));
            });

        assertThat(getCurrentCount(FAILED_WITH_RETRY, MONGODB)).isEqualTo(failedWithRetryCount);

        assertThat(retried.get()).isEqualTo(1);
    }

    @DisplayName("Не повторяет при успехе")
    @Test
    void shouldNotRetryIfSuccess() {
        long successWithoutRetryCount = getCurrentCount(SUCCESS_WITHOUT_RETRY, MONGODB);
        circuitBreakerRegistry.circuitBreaker(MONGODB).transitionToClosedState();

        webClient.get()
            .uri("/api/books")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBodyList(BookDto.class)
            .hasSize(2)
            .value(actual -> {
                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(List.of(EXPECTED_BOOK_DTO, EXPECTED_BOOK_DTO2));
            });

        assertThat(getCurrentCount(SUCCESS_WITHOUT_RETRY, MONGODB)).isEqualTo(successWithoutRetryCount + 1);
    }

    private void checkHealthStatus(String circuitBreakerName, CircuitBreaker.State state) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);
        assertThat(circuitBreaker.getState()).isEqualTo(state);
    }

    private long getCurrentCount(String kind, String backend) {
        Retry.Metrics metrics = retryRegistry.retry(backend).getMetrics();

        if (FAILED_WITH_RETRY.equals(kind)) {
            return metrics.getNumberOfFailedCallsWithRetryAttempt();
        }
        if (SUCCESS_WITHOUT_RETRY.equals(kind)) {
            return metrics.getNumberOfSuccessfulCallsWithoutRetryAttempt();
        }

        return 0;
    }
}
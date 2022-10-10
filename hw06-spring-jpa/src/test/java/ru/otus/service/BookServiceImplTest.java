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
import ru.otus.dao.AuthorDao;
import ru.otus.dao.BookDao;
import ru.otus.dao.GenreDao;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Genre;
import ru.otus.dto.BookDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Тест сервиса книг")
@SpringBootTest
public class BookServiceImplTest {

    private static final Author EXPECTED_AUTHOR = new Author(1L, "Маша Васильева");
    private static final Genre EXPECTED_GENRE = new Genre(1L, "Стихотворение");
    private static final Book EXPECTED_BOOK = new Book(1L, "Короткое стихотворение", EXPECTED_AUTHOR, List.of(EXPECTED_GENRE));
    private static final BookDto EXPECTED_BOOK_DTO = new BookDto(EXPECTED_BOOK);
    private static final long EXPECTED_BOOK_COUNT = 1L;

    @Autowired
    private BookService bookService;

    @MockBean
    private BookDao bookDao;

    @MockBean
    private AuthorDao authorDao;

    @MockBean
    private GenreDao genreDao;

    // чтобы не поднималась база; используем только сервис
    @Configuration
    @Import(BookServiceImpl.class)
    static class BookServiceImplConfiguration {
    }

    @BeforeEach
    public void setUp() {
        when(bookDao.count()).thenReturn(EXPECTED_BOOK_COUNT);
        when(bookDao.save(any())).thenReturn(EXPECTED_BOOK);
        when(bookDao.deleteById(EXPECTED_BOOK.getId())).thenReturn(true);
        when(bookDao.getById(EXPECTED_BOOK.getId())).thenReturn(Optional.of(EXPECTED_BOOK));
        when(bookDao.getAll()).thenReturn(List.of(EXPECTED_BOOK));

        when(authorDao.save(any())).thenReturn(EXPECTED_AUTHOR);
        when(authorDao.getById(EXPECTED_AUTHOR.getId())).thenReturn(Optional.of(EXPECTED_AUTHOR));
        when(authorDao.getByName(EXPECTED_AUTHOR.getName())).thenReturn(Optional.of(EXPECTED_AUTHOR));

        when(genreDao.save(any())).thenReturn(EXPECTED_GENRE);
        when(genreDao.getById(EXPECTED_GENRE.getId())).thenReturn(Optional.of(EXPECTED_GENRE));
        when(genreDao.getByTitle(EXPECTED_GENRE.getTitle())).thenReturn(Optional.of(EXPECTED_GENRE));
        when(genreDao.getByTitles(List.of(EXPECTED_GENRE.getTitle()))).thenReturn(Map.of(EXPECTED_GENRE.getTitle(), EXPECTED_GENRE));
    }

    @DisplayName("Возвращает ожидаемое количество книг")
    @Test
    void shouldReturnExpectedBookCount() {
        long booksCount = bookService.count();
        assertThat(booksCount).isEqualTo(EXPECTED_BOOK_COUNT);
    }

    @DisplayName("Добавляет книгу в БД")
    @Test
    void shouldInsertBook() {
        BookDto expectedBook = EXPECTED_BOOK_DTO;

        expectedBook = bookService.insert(expectedBook);

        Optional<BookDto> actualBook = bookService.getById(expectedBook.getId());
        compareBooks(actualBook, expectedBook);
    }

    @DisplayName("Обновляет книгу в БД")
    @Test
    void shouldUpdateBook() {
        BookDto initialBook = EXPECTED_BOOK_DTO;

        Optional<BookDto> bookBeforeUpdate = bookService.getById(initialBook.getId());
        compareBooks(bookBeforeUpdate, initialBook);

        String randomTxt = UUID.randomUUID().toString();

        Author authorNew = new Author(2L, "My author " + randomTxt);
        when(authorDao.getByName(authorNew.getName())).thenReturn(Optional.empty());
        when(authorDao.save(any())).thenReturn(authorNew);

        Genre genreNew = new Genre(2L, "My genre " + randomTxt);
        when(genreDao.getByTitle(genreNew.getTitle())).thenReturn(Optional.empty());
        when(genreDao.save(any())).thenReturn(genreNew);

        Book updatedBook = new Book(initialBook.getId(), "My title " + randomTxt, authorNew, List.of(genreNew));
        BookDto updatedBookDto = new BookDto(updatedBook);
        when(bookDao.getById(initialBook.getId())).thenReturn(Optional.of(updatedBook));

        bookService.update(updatedBookDto);

        Optional<BookDto> actualBook = bookService.getById(initialBook.getId());
        compareBooks(actualBook, updatedBookDto);
    }

    @DisplayName("Удаляет книгу из БД")
    @Test
    void shouldDeleteBook() {
        BookDto expectedBook = EXPECTED_BOOK_DTO;

        when(bookDao.getById(expectedBook.getId())).thenReturn(Optional.empty());

        boolean deleted = bookService.deleteById(expectedBook.getId());
        assertThat(deleted).isEqualTo(true);

        Optional<BookDto> actualBook = bookService.getById(expectedBook.getId());
        assertThat(actualBook).isEmpty();
    }

    @DisplayName("Возвращает ожидаемую книгу по id")
    @Test
    void shouldReturnExpectedBookById() {
        BookDto expectedBook = EXPECTED_BOOK_DTO;
        Optional<BookDto> actualBook = bookService.getById(expectedBook.getId());
        compareBooks(actualBook, expectedBook);
    }

    @DisplayName("Возвращает ожидаемый список книг")
    @Test
    void shouldReturnExpectedBooks() {
        List<BookDto> actualBooksList = bookService.getAll();
        assertThat(actualBooksList)
            .usingRecursiveFieldByFieldElementComparator(RecursiveComparisonConfiguration.builder().build())
            .containsExactlyInAnyOrderElementsOf(List.of(EXPECTED_BOOK_DTO));
    }

    private void compareBooks(Optional<BookDto> actualBook, BookDto expectedBook) {
        assertThat(actualBook)
            .isNotEmpty()
            .get()
            .usingRecursiveComparison()
            .isEqualTo(expectedBook);
    }
}

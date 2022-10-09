package ru.otus.service;

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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@DisplayName("Тест сервиса книг")
@SpringBootTest
public class BookServiceImplTest {

    private static final Author EXPECTED_AUTHOR = new Author(1L, "Маша Васильева");
    private static final Genre EXPECTED_GENRE = new Genre(1L, "Стихотворение");
    private static final Book EXPECTED_BOOK = new Book(1L, "Короткое стихотворение", EXPECTED_AUTHOR, EXPECTED_GENRE);
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
        when(bookDao.insert(any())).thenReturn(EXPECTED_BOOK.getId());
        when(bookDao.update(any())).thenReturn(true);
        when(bookDao.deleteById(anyLong())).thenReturn(true);
        when(bookDao.getById(anyLong())).thenReturn(Optional.of(EXPECTED_BOOK));
        when(bookDao.getAll()).thenReturn(List.of(EXPECTED_BOOK));

        when(authorDao.insert(any())).thenReturn(EXPECTED_AUTHOR.getId());
        when(authorDao.getById(anyLong())).thenReturn(Optional.of(EXPECTED_AUTHOR));
        when(authorDao.getByName(anyString())).thenReturn(Optional.of(EXPECTED_AUTHOR));

        when(genreDao.insert(any())).thenReturn(EXPECTED_GENRE.getId());
        when(genreDao.getById(anyLong())).thenReturn(Optional.of(EXPECTED_GENRE));
        when(genreDao.getByTitle(anyString())).thenReturn(Optional.of(EXPECTED_GENRE));
    }

    @DisplayName("Возвращает ожидаемое количество книг")
    @Test
    void shouldReturnExpectedBookCount() {
        long booksCount = bookDao.count();
        assertThat(booksCount).isEqualTo(EXPECTED_BOOK_COUNT);
    }

    @DisplayName("Добавляет книгу в БД")
    @Test
    void shouldInsertBook() {
        Book expectedBook = EXPECTED_BOOK;

        long insertedId = bookDao.insert(expectedBook);
        expectedBook.setId(insertedId);

        Optional<Book> actualBook = bookDao.getById(insertedId);
        compareBooks(actualBook, expectedBook);
    }

    @DisplayName("Обновляет книгу в БД")
    @Test
    void shouldUpdateBook() {
        Book initialBook = EXPECTED_BOOK;

        Optional<Book> bookBeforeUpdate = bookDao.getById(initialBook.getId());
        compareBooks(bookBeforeUpdate, initialBook);

        String randomTxt = UUID.randomUUID().toString();

        Author authorNew = new Author(2L, "My author " + randomTxt);
        when(authorDao.getByName(anyString())).thenReturn(Optional.empty());
        when(authorDao.insert(any())).thenReturn(authorNew.getId());

        Genre genreNew = new Genre(2L, "My genre " + randomTxt);
        when(genreDao.getByTitle(anyString())).thenReturn(Optional.empty());
        when(genreDao.insert(any())).thenReturn(genreNew.getId());

        Book updatedBook = new Book(initialBook.getId(), "My title " + randomTxt, authorNew, genreNew);
        when(bookDao.getById(anyLong())).thenReturn(Optional.of(updatedBook));

        boolean updated = bookDao.update(updatedBook);
        assertThat(updated).isEqualTo(true);

        Optional<Book> actualBook = bookDao.getById(initialBook.getId());
        compareBooks(actualBook, updatedBook);
    }

    @DisplayName("Удаляет книгу из БД")
    @Test
    void shouldDeleteBook() {
        Book expectedBook = EXPECTED_BOOK;

        when(bookDao.getById(anyLong())).thenReturn(Optional.empty());

        boolean deleted = bookDao.deleteById(expectedBook.getId());
        assertThat(deleted).isEqualTo(true);

        Optional<Book> actualBook = bookDao.getById(expectedBook.getId());
        assertThat(actualBook).isEmpty();
    }

    @DisplayName("Возвращает ожидаемую книгу по id")
    @Test
    void shouldReturnExpectedBookById() {
        Book expectedBook = EXPECTED_BOOK;
        Optional<Book> actualBook = bookDao.getById(expectedBook.getId());
        compareBooks(actualBook, expectedBook);
    }

    @DisplayName("Возвращает ожидаемый список книг")
    @Test
    void shouldReturnExpectedBooks() {
        List<Book> actualBooksList = bookDao.getAll();
        assertThat(actualBooksList).containsExactlyInAnyOrderElementsOf(List.of(EXPECTED_BOOK));
    }

    private void compareBooks(Optional<Book> actualBook, Book expectedBook) {
        assertThat(actualBook)
            .isNotEmpty()
            .get()
            .usingRecursiveComparison()
            .isEqualTo(expectedBook);
    }
}

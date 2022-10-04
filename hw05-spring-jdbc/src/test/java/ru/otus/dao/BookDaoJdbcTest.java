package ru.otus.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Genre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тест DAO для книг")
@JdbcTest
@Import(BookDaoJdbc.class)
class BookDaoJdbcTest {

    private static final List<Author> EXPECTED_AUTHORS = List.of(
        new Author(1L, "Катя Петрова"), new Author(2L, "Женя Максимова")
    );
    private static final List<Genre> EXPECTED_GENRES = List.of(
        new Genre(1L, "Детектив"), new Genre(2L, "Фантастика")
    );
    private static final List<Book> EXPECTED_BOOKS = List.of(
        new Book(1L, "Мой детектив", EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0)),
        new Book(2L, "Необычная фантастика", EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(1)),
        new Book(3L, "Смешной детектив", EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(0))
    );

    @Autowired
    private BookDaoJdbc bookDao;

    @DisplayName("Возвращает ожидаемое количество книг после инициализации")
    @Test
    void shouldReturnExpectedBookCount() {
        long booksCountAfterInit = bookDao.count();
        assertThat(booksCountAfterInit).isEqualTo(EXPECTED_BOOKS.size());
    }

    @DisplayName("Добавляет книгу в БД")
    @Test
    void shouldInsertBook() {
        long countBeforeInsert = bookDao.count();

        String randomTxt = UUID.randomUUID().toString();
        Book expectedBook = new Book("My title " + randomTxt, EXPECTED_AUTHORS.get(0), EXPECTED_GENRES.get(0));

        long insertedId = bookDao.insert(expectedBook);
        expectedBook.setId(insertedId);

        long countAfterInsert = bookDao.count();
        assertThat(countAfterInsert).isEqualTo(countBeforeInsert + 1);

        Optional<Book> actualBook = bookDao.getById(insertedId);
        compareBooks(actualBook, expectedBook);
    }

    @DisplayName("Обновляет книгу в БД")
    @Test
    void shouldUpdateBook() {
        Book initialBook = EXPECTED_BOOKS.get(0);

        long countBeforeUpdate = bookDao.count();

        Optional<Book> bookBeforeUpdate = bookDao.getById(initialBook.getId());
        compareBooks(bookBeforeUpdate, initialBook);

        String randomTxt = UUID.randomUUID().toString();
        Book updatedBook = new Book(initialBook.getId(), "My title " + randomTxt, EXPECTED_AUTHORS.get(1), EXPECTED_GENRES.get(1));

        boolean updated = bookDao.update(updatedBook);
        assertThat(updated).isEqualTo(true);

        long countAfterUpdate = bookDao.count();
        assertThat(countAfterUpdate).isEqualTo(countBeforeUpdate);

        Optional<Book> actualBook = bookDao.getById(initialBook.getId());
        compareBooks(actualBook, updatedBook);
    }

    @DisplayName("Удаляет книгу из БД")
    @Test
    void shouldDeleteBook() {
        Book expectedBook = EXPECTED_BOOKS.get(0);

        long countBeforeDelete = bookDao.count();

        boolean deleted = bookDao.deleteById(expectedBook.getId());
        assertThat(deleted).isEqualTo(true);

        long countAfterDelete = bookDao.count();
        assertThat(countAfterDelete).isEqualTo(countBeforeDelete - 1);

        Optional<Book> actualBook = bookDao.getById(expectedBook.getId());
        assertThat(actualBook).isEmpty();
    }

    @DisplayName("Возвращает ожидаемую книгу по id")
    @Test
    void shouldReturnExpectedBookById() {
        Book expectedBook = EXPECTED_BOOKS.get(0);
        Optional<Book> actualBook = bookDao.getById(expectedBook.getId());
        compareBooks(actualBook, expectedBook);
    }

    @DisplayName("Возвращает ожидаемый список книг")
    @Test
    void shouldReturnExpectedBooks() {
        List<Book> actualBooksList = bookDao.getAll();
        assertThat(actualBooksList).containsExactlyInAnyOrderElementsOf(EXPECTED_BOOKS);
    }

    private void compareBooks(Optional<Book> actualBook, Book expectedBook) {
        assertThat(actualBook)
            .isNotEmpty()
            .get()
            .usingRecursiveComparison()
            .isEqualTo(expectedBook);
    }
}
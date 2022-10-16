package ru.otus.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Genre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тест DAO для книг")
@DataJpaTest
@Import(BookDaoJpa.class)
class BookDaoJpaTest {

    private static final List<Author> EXPECTED_AUTHORS = List.of(
        new Author(1L, "Катя Петрова"), new Author(2L, "Женя Максимова")
    );
    private static final List<Genre> EXPECTED_GENRES = List.of(
        new Genre(1L, "Детектив"), new Genre(2L, "Фантастика")
    );
    private static final List<Book> EXPECTED_BOOKS = List.of(
        new Book(1L, "Мой детектив", EXPECTED_AUTHORS.get(0), List.of(EXPECTED_GENRES.get(0)), null),
        new Book(2L, "Необычная фантастика", EXPECTED_AUTHORS.get(1), List.of(EXPECTED_GENRES.get(1)), null),
        new Book(3L, "Смешной детектив", EXPECTED_AUTHORS.get(1), EXPECTED_GENRES, null)
    );

    @Autowired
    private BookDaoJpa bookDao;

    @Autowired
    private TestEntityManager em;

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

        Author author = em.find(Author.class, EXPECTED_AUTHORS.get(0).getId());
        Genre genre = em.find(Genre.class, EXPECTED_GENRES.get(0).getId());

        String randomTxt = UUID.randomUUID().toString();
        Book expectedBook = new Book(null, "My title " + randomTxt, author, List.of(genre), null);

        expectedBook = bookDao.save(expectedBook);

        long countAfterInsert = bookDao.count();
        assertThat(countAfterInsert).isEqualTo(countBeforeInsert + 1);

        Optional<Book> actualBook = bookDao.getById(expectedBook.getId());

        assertThat(actualBook).isNotEmpty();
        compareBooks(actualBook.get(), expectedBook);
    }

    @DisplayName("Обновляет книгу в БД")
    @Test
    void shouldUpdateBook() {
        Book initialBook = em.find(Book.class, EXPECTED_BOOKS.get(0).getId());

        long countBeforeUpdate = bookDao.count();

        Optional<Book> bookBeforeUpdate = bookDao.getById(initialBook.getId());
        assertThat(bookBeforeUpdate).isNotEmpty();
        compareBooks(bookBeforeUpdate.get(), initialBook);

        Author author = em.find(Author.class, EXPECTED_AUTHORS.get(1).getId());
        Genre genre = em.find(Genre.class, EXPECTED_GENRES.get(1).getId());
        String randomTxt = UUID.randomUUID().toString();
        Book updatedBook = new Book(initialBook.getId(), "My title " + randomTxt, author, List.of(genre), null);

        bookDao.save(updatedBook);

        long countAfterUpdate = bookDao.count();
        assertThat(countAfterUpdate).isEqualTo(countBeforeUpdate);

        Book actualBook = em.find(Book.class, EXPECTED_BOOKS.get(0).getId());
        compareBooks(actualBook, updatedBook);
    }

    @DisplayName("Удаляет книгу из БД")
    @Test
    void shouldDeleteBook() {
        Book expectedBook = em.find(Book.class, EXPECTED_BOOKS.get(0).getId());

        long countBeforeDelete = bookDao.count();

        boolean deleted = bookDao.deleteById(expectedBook.getId());
        assertThat(deleted).isEqualTo(true);

        long countAfterDelete = bookDao.count();
        assertThat(countAfterDelete).isEqualTo(countBeforeDelete - 1);

        Book actualBook = em.find(Book.class, EXPECTED_BOOKS.get(0).getId());
        assertThat(actualBook).isNull();
    }

    @DisplayName("Возвращает ожидаемую книгу по id")
    @Test
    void shouldReturnExpectedBookById() {
        long expectedBookId = EXPECTED_BOOKS.get(0).getId();

        Book expectedBook = em.find(Book.class, expectedBookId);
        Optional<Book> actualBook = bookDao.getById(expectedBookId);

        assertThat(actualBook).isNotEmpty();
        compareBooks(actualBook.get(), expectedBook);
    }

    @DisplayName("Возвращает ожидаемый список книг")
    @Test
    void shouldReturnExpectedBooks() {
        List<Book> actualBooks = bookDao.getAll();
        assertThat(actualBooks).containsExactlyInAnyOrderElementsOf(EXPECTED_BOOKS);
    }

    private void compareBooks(Book actualBook, Book expectedBook) {
        assertThat(actualBook)
            .usingRecursiveComparison()
            .isEqualTo(expectedBook);
    }
}
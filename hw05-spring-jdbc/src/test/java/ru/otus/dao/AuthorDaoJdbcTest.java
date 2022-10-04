package ru.otus.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.domain.Author;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тест DAO для авторов")
@JdbcTest
@Import(AuthorDaoJdbc.class)
class AuthorDaoJdbcTest {

    private static final List<Author> EXPECTED_AUTHORS = List.of(
        new Author(1L, "Катя Петрова"), new Author(2L, "Женя Максимова")
    );

    @Autowired
    private AuthorDaoJdbc authorDao;

    @DisplayName("Добавляет автора в БД")
    @Test
    void shouldInsertAuthor() {
        String randomTxt = UUID.randomUUID().toString();
        Author expectedAuthor= new Author("My name " + randomTxt);

        long insertedId = authorDao.insert(expectedAuthor);
        expectedAuthor.setId(insertedId);

        Optional<Author> actualAuthor = authorDao.getById(insertedId);
        compareAuthor(actualAuthor, expectedAuthor);
    }

    @DisplayName("Возвращает ожидаемого автора по id")
    @Test
    void shouldReturnExpectedAuthorById() {
        Author expectedAuthor = EXPECTED_AUTHORS.get(0);
        Optional<Author> actualAuthor = authorDao.getById(expectedAuthor.getId());
        compareAuthor(actualAuthor, expectedAuthor);
    }

    @DisplayName("Возвращает ожидаемого автора по имени")
    @Test
    void shouldReturnExpectedAuthorByName() {
        Author expectedAuthor = EXPECTED_AUTHORS.get(0);
        Optional<Author> actualAuthor = authorDao.getByName(expectedAuthor.getName());
        compareAuthor(actualAuthor, expectedAuthor);
    }

    private void compareAuthor(Optional<Author> actualAuthor, Author expectedAuthor) {
        assertThat(actualAuthor)
            .isNotEmpty()
            .get()
            .usingRecursiveComparison()
            .isEqualTo(expectedAuthor);
    }
}
package ru.otus.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.domain.Author;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тест DAO для авторов")
@DataJpaTest
@Import(AuthorDaoJpa.class)
class AuthorDaoJpaTest {

    private static final List<Author> EXPECTED_AUTHORS = List.of(
        new Author(1L, "Катя Петрова"), new Author(2L, "Женя Максимова")
    );

    @Autowired
    private AuthorDaoJpa authorDao;

    @Autowired
    private TestEntityManager em;

    @DisplayName("Добавляет автора в БД")
    @Test
    void shouldInsertAuthor() {
        String randomTxt = UUID.randomUUID().toString();
        Author expectedAuthor = new Author(null, "My name " + randomTxt);

        expectedAuthor = authorDao.save(expectedAuthor);

        Author actualAuthor = em.find(Author.class, expectedAuthor.getId());
        compareAuthors(actualAuthor, expectedAuthor);
    }

    @DisplayName("Возвращает ожидаемого автора по id")
    @Test
    void shouldReturnExpectedAuthorById() {
        long expectedAuthorId = EXPECTED_AUTHORS.get(0).getId();

        Author expectedAuthor = em.find(Author.class, expectedAuthorId);
        Optional<Author> actualAuthor = authorDao.getById(expectedAuthorId);

        assertThat(actualAuthor).isNotEmpty();
        compareAuthors(actualAuthor.get(), expectedAuthor);
    }

    @DisplayName("Возвращает ожидаемого автора по имени")
    @Test
    void shouldReturnExpectedAuthorByName() {
        Author expectedAuthor = em.find(Author.class, EXPECTED_AUTHORS.get(0).getId());
        Optional<Author> actualAuthor = authorDao.getByName(expectedAuthor.getName());
        assertThat(actualAuthor).isNotEmpty();
        compareAuthors(actualAuthor.get(), expectedAuthor);
    }

    private void compareAuthors(Author actualAuthor, Author expectedAuthor) {
        assertThat(actualAuthor)
            .usingRecursiveComparison()
            .isEqualTo(expectedAuthor);
    }
}
package ru.otus.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.domain.Genre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тест DAO для жанров")
@JdbcTest
@Import(GenreDaoJdbc.class)
class GenreDaoJdbcTest {

    private static final List<Genre> EXPECTED_GENRES = List.of(
        new Genre(1L, "Детектив"), new Genre(2L, "Фантастика")
    );

    @Autowired
    private GenreDaoJdbc genreDao;

    @DisplayName("Добавляет жанр в БД")
    @Test
    void shouldInsertGenre() {
        String randomTxt = UUID.randomUUID().toString();
        Genre expectedGenre= new Genre("My title " + randomTxt);

        long insertedId = genreDao.insert(expectedGenre);
        expectedGenre.setId(insertedId);

        Optional<Genre> actualGenre = genreDao.getById(insertedId);
        compareGenre(actualGenre, expectedGenre);
    }

    @DisplayName("Возвращает ожидаемый жанр по id")
    @Test
    void shouldReturnExpectedGenreById() {
        Genre expectedGenre = EXPECTED_GENRES.get(0);
        Optional<Genre> actualGenre = genreDao.getById(expectedGenre.getId());
        compareGenre(actualGenre, expectedGenre);
    }

    @DisplayName("Возвращает ожидаемый жанр по имени")
    @Test
    void shouldReturnExpectedGenreByName() {
        Genre expectedGenre = EXPECTED_GENRES.get(0);
        Optional<Genre> actualGenre = genreDao.getByTitle(expectedGenre.getTitle());
        compareGenre(actualGenre, expectedGenre);
    }

    private void compareGenre(Optional<Genre> actualGenre, Genre expectedGenre) {
        assertThat(actualGenre)
            .isNotEmpty()
            .get()
            .usingRecursiveComparison()
            .isEqualTo(expectedGenre);
    }
}
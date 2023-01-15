package ru.otus.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.domain.Genre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тест DAO для жанров")
@DataJpaTest
class GenreDaoJpaTest {

    private static final List<Genre> EXPECTED_GENRES = List.of(
        new Genre(1L, "Детектив"), new Genre(2L, "Фантастика")
    );

    @Autowired
    private GenreDao genreDao;

    @Autowired
    private TestEntityManager em;

    @DisplayName("Добавляет жанр в БД")
    @Test
    void shouldInsertGenre() {
        String randomTxt = UUID.randomUUID().toString();
        Genre expectedGenre = new Genre(null, "My title " + randomTxt);

        expectedGenre = genreDao.save(expectedGenre);

        Genre actualGenre = em.find(Genre.class, expectedGenre.getId());
        compareGenres(actualGenre, expectedGenre);
    }

    @DisplayName("Возвращает ожидаемый жанр по id")
    @Test
    void shouldReturnExpectedGenreById() {
        long expectedGenreId = EXPECTED_GENRES.get(0).getId();

        Genre expectedGenre = em.find(Genre.class, expectedGenreId);
        Optional<Genre> actualGenre = genreDao.findById(expectedGenreId);

        assertThat(actualGenre).isNotEmpty();
        compareGenres(actualGenre.get(), expectedGenre);
    }

    @DisplayName("Возвращает ожидаемый жанр по названию")
    @Test
    void shouldReturnExpectedGenreByTitle() {
        Genre expectedGenre = em.find(Genre.class, EXPECTED_GENRES.get(0).getId());
        Optional<Genre> actualGenre = genreDao.findByTitle(expectedGenre.getTitle());
        assertThat(actualGenre).isNotEmpty();
        compareGenres(actualGenre.get(), expectedGenre);
    }

    @DisplayName("Возвращает ожидаемый жанры по списку названий")
    @Test
    void shouldReturnExpectedGenreByTitles() {
        Genre expectedGenre1 = em.find(Genre.class, EXPECTED_GENRES.get(0).getId());
        Genre expectedGenre2 = em.find(Genre.class, EXPECTED_GENRES.get(1).getId());
        List<Genre> actualGenres = genreDao.findByTitleIn(List.of(expectedGenre1.getTitle(), expectedGenre2.getTitle(), "TEST"));
        assertThat(actualGenres).isNotEmpty()
            .containsExactlyInAnyOrder(expectedGenre1, expectedGenre2);
    }

    private void compareGenres(Genre actualGenre, Genre expectedGenre) {
        assertThat(actualGenre)
            .usingRecursiveComparison()
            .isEqualTo(expectedGenre);
    }
}
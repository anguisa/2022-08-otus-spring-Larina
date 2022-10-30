package ru.otus.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.dao.GenreDao;
import ru.otus.domain.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Тест сервиса жанров")
@SpringBootTest
public class GenreServiceImplTest {

    private static final Genre EXPECTED_GENRE_1 = new Genre("1", "Стихотворение");
    private static final Genre EXPECTED_GENRE_2 = new Genre("2", "Детектив");

    @Autowired
    private GenreService genreService;

    @MockBean
    private GenreDao genreDao;

    // чтобы не поднималась база; используем только сервис
    @Configuration
    @Import(GenreServiceImpl.class)
    static class GenreServiceImplConfiguration {
    }

    @DisplayName("Находит жанр по названию и создаёт, если не находит")
    @Test
    void shouldFindGenreByTitleAndCreateIfNotFound() {
        Genre genreToSave = new Genre(EXPECTED_GENRE_2.getTitle());
        when(genreDao.findByTitleIn(List.of(EXPECTED_GENRE_1.getTitle(), EXPECTED_GENRE_2.getTitle()))).thenReturn(List.of(EXPECTED_GENRE_1));
        when(genreDao.save(genreToSave)).thenReturn(EXPECTED_GENRE_2);

        List<Genre> genres = genreService.findByTitleInOrCreate(List.of(EXPECTED_GENRE_1.getTitle(), EXPECTED_GENRE_2.getTitle()));
        assertThat(genres).isNotEmpty()
            .containsExactlyInAnyOrder(EXPECTED_GENRE_1, EXPECTED_GENRE_2);

        verify(genreDao, times(1)).save(any());
    }

}

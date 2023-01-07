package ru.otus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.domain.Genre;
import ru.otus.dto.GenreDto;
import ru.otus.service.GenreService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Тест контроллера жанров")
@WebMvcTest(GenreController.class)
class GenreControllerTest {

    private static final Genre EXPECTED_GENRE = new Genre(1L, "Стихотворение");
    private static final Genre EXPECTED_GENRE2 = new Genre(2L, "Проза");
    private static final GenreDto EXPECTED_GENRE_DTO = new GenreDto(EXPECTED_GENRE.getId(), EXPECTED_GENRE.getTitle());
    private static final GenreDto EXPECTED_GENRE_DTO2 = new GenreDto(EXPECTED_GENRE2.getId(), EXPECTED_GENRE2.getTitle());

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private GenreService genreService;

    @BeforeEach
    public void setUp() {
        when(genreService.findAll()).thenReturn(List.of(EXPECTED_GENRE_DTO, EXPECTED_GENRE_DTO2));
    }

    @DisplayName("Возвращает ожидаемый список жанров")
    @Test
    void shouldReturnExpectedGenres() throws Exception {
        List<GenreDto> expected = List.of(EXPECTED_GENRE_DTO, EXPECTED_GENRE_DTO2);

        mvc.perform(get("/api/genres"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

}
package ru.otus.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.dao.AuthorDao;
import ru.otus.domain.Author;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Тест сервиса авторов")
@SpringBootTest
public class AuthorServiceImplTest {

    private static final Author EXPECTED_AUTHOR = new Author("1", "Маша Васильева");

    @Autowired
    private AuthorService authorService;

    @MockBean
    private AuthorDao authorDao;

    // чтобы не поднималась база; используем только сервис
    @Configuration
    @Import(AuthorServiceImpl.class)
    static class AuthorServiceImplConfiguration {
    }

    @DisplayName("Находит автора по имени")
    @Test
    void shouldFindAuthorByName() {
        when(authorDao.findByName(EXPECTED_AUTHOR.getName())).thenReturn(Optional.of(EXPECTED_AUTHOR));

        Author actualAuthor = authorService.findByNameOrCreate(EXPECTED_AUTHOR.getName());
        assertThat(actualAuthor).isEqualTo(EXPECTED_AUTHOR);

        verify(authorDao, times(0)).save(any());
    }

    @DisplayName("Создаёт автора, если не находит по имени")
    @Test
    void shouldCreateAuthorIfNotFoundByName() {
        Author authorToSave = new Author(EXPECTED_AUTHOR.getName());
        when(authorDao.findByName(EXPECTED_AUTHOR.getName())).thenReturn(Optional.empty());
        when(authorDao.save(authorToSave)).thenReturn(EXPECTED_AUTHOR);

        Author actualAuthor = authorService.findByNameOrCreate(EXPECTED_AUTHOR.getName());
        assertThat(actualAuthor).isEqualTo(EXPECTED_AUTHOR);

        verify(authorDao, times(1)).save(authorToSave);
    }
}

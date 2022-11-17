package ru.otus.service;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.otus.dao.BookDao;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Genre;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.GenreDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("Тест сервиса книг")
@SpringBootTest
public class BookServiceImplTest {

    private static final Author EXPECTED_AUTHOR = new Author(1L, "Маша Васильева");
    private static final AuthorDto EXPECTED_AUTHOR_DTO = new AuthorDto(EXPECTED_AUTHOR.getId(), EXPECTED_AUTHOR.getName());
    private static final Genre EXPECTED_GENRE = new Genre(1L, "Стихотворение");
    private static final GenreDto EXPECTED_GENRE_DTO = new GenreDto(EXPECTED_GENRE.getId(), EXPECTED_GENRE.getTitle());
    private static final Book EXPECTED_BOOK = new Book(1L, "Короткое стихотворение", EXPECTED_AUTHOR, List.of(EXPECTED_GENRE), null);
    private static final BookDto EXPECTED_BOOK_DTO = new BookDto(EXPECTED_BOOK.getId(), EXPECTED_BOOK.getTitle(), EXPECTED_AUTHOR_DTO, List.of(EXPECTED_GENRE_DTO));

    @Autowired
    private BookService bookService;

    @MockBean
    private BookDao bookDao;

    // чтобы не поднималась база; используем только сервис и конвертеры
    @Configuration
    @Import(BookServiceImpl.class)
    @ComponentScan("ru.otus.dto.converter")
    static class BookServiceImplConfiguration {
    }

    @BeforeEach
    public void setUp() {
        when(bookDao.save(any())).thenReturn(EXPECTED_BOOK);
        when(bookDao.findById(EXPECTED_BOOK.getId())).thenReturn(Optional.of(EXPECTED_BOOK));
        when(bookDao.findAll()).thenReturn(List.of(EXPECTED_BOOK));
    }

    @DisplayName("Добавляет книгу в БД")
    @Test
    void shouldInsertBook() {
        BookDto expectedBook = EXPECTED_BOOK_DTO;

        expectedBook = bookService.insert(expectedBook);

        Optional<BookDto> actualBook = bookService.findById(expectedBook.getId());
        compareBooks(actualBook, expectedBook);
    }

    @DisplayName("Обновляет книгу в БД")
    @Test
    void shouldUpdateBook() {
        BookDto initialBook = EXPECTED_BOOK_DTO;

        Optional<BookDto> bookBeforeUpdate = bookService.findById(initialBook.getId());
        compareBooks(bookBeforeUpdate, initialBook);

        String randomTxt = UUID.randomUUID().toString();

        Author authorNew = new Author(2L, "My author " + randomTxt);
        Genre genreNew = new Genre(2L, "My genre " + randomTxt);

        Book updatedBook = new Book(initialBook.getId(), "My title " + randomTxt, authorNew, List.of(genreNew), null);
        BookDto updatedBookDto = new BookDto(updatedBook.getId(), updatedBook.getTitle(),
            new AuthorDto(authorNew.getId(), authorNew.getName()), List.of(new GenreDto(genreNew.getId(), genreNew.getTitle())));
        when(bookDao.findById(initialBook.getId())).thenReturn(Optional.of(updatedBook));

        bookService.update(updatedBookDto);

        Optional<BookDto> actualBook = bookService.findById(initialBook.getId());
        compareBooks(actualBook, updatedBookDto);
    }

    @DisplayName("Удаляет книгу из БД")
    @Test
    void shouldDeleteBook() {
        BookDto expectedBook = EXPECTED_BOOK_DTO;

        when(bookDao.findById(expectedBook.getId())).thenReturn(Optional.empty());

        bookService.deleteById(expectedBook.getId());

        Optional<BookDto> actualBook = bookService.findById(expectedBook.getId());
        assertThat(actualBook).isEmpty();
    }

    @DisplayName("Возвращает ожидаемую книгу по id")
    @Test
    void shouldReturnExpectedBookById() {
        BookDto expectedBook = EXPECTED_BOOK_DTO;
        Optional<BookDto> actualBook = bookService.findById(expectedBook.getId());
        compareBooks(actualBook, expectedBook);
    }

    @DisplayName("Возвращает ожидаемый список книг")
    @Test
    void shouldReturnExpectedBooks() {
        List<BookDto> actualBooksList = bookService.findAll();
        assertThat(actualBooksList)
            .usingRecursiveFieldByFieldElementComparator(RecursiveComparisonConfiguration.builder().build())
            .containsExactlyInAnyOrderElementsOf(List.of(EXPECTED_BOOK_DTO));
    }

    private void compareBooks(Optional<BookDto> actualBook, BookDto expectedBook) {
        assertThat(actualBook)
            .isNotEmpty()
            .get()
            .usingRecursiveComparison()
            .isEqualTo(expectedBook);
    }
}

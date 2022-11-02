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
import ru.otus.dao.CommentDao;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.domain.Genre;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.GenreDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Тест сервиса книг")
@SpringBootTest
public class BookServiceImplTest {

    private static final Author EXPECTED_AUTHOR = new Author("1", "Маша Васильева");
    private static final AuthorDto EXPECTED_AUTHOR_DTO = new AuthorDto(EXPECTED_AUTHOR.getId(), EXPECTED_AUTHOR.getName());
    private static final Genre EXPECTED_GENRE = new Genre("2", "Стихотворение");
    private static final GenreDto EXPECTED_GENRE_DTO = new GenreDto(EXPECTED_GENRE.getId(), EXPECTED_GENRE.getTitle());
    private static final Book EXPECTED_BOOK = new Book("3", "Короткое стихотворение", EXPECTED_AUTHOR,
        List.of(EXPECTED_GENRE), List.of(new Comment("4", "Комментарий1"), new Comment("5", "Комментарий2")));
    private static final BookDto EXPECTED_BOOK_DTO = new BookDto(EXPECTED_BOOK.getId(), EXPECTED_BOOK.getTitle(), EXPECTED_AUTHOR_DTO, List.of(EXPECTED_GENRE_DTO));
    private static final long EXPECTED_BOOK_COUNT = 1L;

    @Autowired
    private BookService bookService;

    @MockBean
    private BookDao bookDao;

    @MockBean
    private CommentDao commentDao;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    // чтобы не поднималась база; используем только сервис и конвертеры
    @Configuration
    @Import(BookServiceImpl.class)
    @ComponentScan("ru.otus.dto.converter")
    static class BookServiceImplConfiguration {
    }

    @BeforeEach
    public void setUp() {
        when(bookDao.count()).thenReturn(EXPECTED_BOOK_COUNT);
        when(bookDao.save(any())).thenReturn(EXPECTED_BOOK);
        when(bookDao.findById(EXPECTED_BOOK.getId())).thenReturn(Optional.of(EXPECTED_BOOK));
        when(bookDao.findAll()).thenReturn(List.of(EXPECTED_BOOK));

        when(authorService.findByNameOrCreate(EXPECTED_AUTHOR.getName())).thenReturn(EXPECTED_AUTHOR);

        when(genreService.findByTitleInOrCreate(List.of(EXPECTED_GENRE.getTitle()))).thenReturn(List.of(EXPECTED_GENRE));
    }

    @DisplayName("Возвращает ожидаемое количество книг")
    @Test
    void shouldReturnExpectedBookCount() {
        long booksCount = bookService.count();
        assertThat(booksCount).isEqualTo(EXPECTED_BOOK_COUNT);
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

        Author authorNew = new Author("10", "My author " + randomTxt);
        when(authorService.findByNameOrCreate(authorNew.getName())).thenReturn(authorNew);

        Genre genreNew = new Genre("20", "My genre " + randomTxt);
        when(genreService.findByTitleInOrCreate(List.of(genreNew.getTitle()))).thenReturn(List.of(genreNew));

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

        bookService.deleteById(expectedBook.getId());

        verify(commentDao, times(1)).deleteAllById(List.of(EXPECTED_BOOK.getComments().get(0).getId(), EXPECTED_BOOK.getComments().get(1).getId()));

        when(bookDao.findById(expectedBook.getId())).thenReturn(Optional.empty());

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

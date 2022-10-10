package ru.otus.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.GenreDto;
import ru.otus.service.BookService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
public class BookShellService {

    private final BookService bookService;

    public BookShellService(BookService bookService) {
        this.bookService = bookService;
    }

    // Пример использования: i --t 'Моя книга' --a 'Мой автор' --g 'Жанр 1' 'Жанр 2'
    @ShellMethod(value = "Insert new book", key = {"insert", "i"})
    public String insertBook(@ShellOption(help = "Book title", value = {"--title", "--t"}) String bookTitle,
                             @ShellOption(help = "Author name", value = {"--author", "--a"}) String authorName,
                             @ShellOption(help = "Genre titles (with space as delimiter)", value = {"--genre", "--g"}) String[] genreTitles) {
        BookDto book = new BookDto(bookTitle, new AuthorDto(authorName), Arrays.stream(genreTitles).map(GenreDto::new).collect(Collectors.toList()));
        return bookService.insert(book).toString();
    }

    // Пример использования: u --i 6 --t 'Моя книга 1' --a 'Мой автор 1' --g 'Жанр 2' 'Жанр 3'
    @ShellMethod(value = "Update book", key = {"update", "u"})
    public String updateBook(@ShellOption(help = "Book id", value = {"--id", "--i"}) long bookId,
                             @ShellOption(help = "Book title", value = {"--title", "--t"}) String bookTitle,
                             @ShellOption(help = "Author name", value = {"--author", "--a"}) String authorName,
                             @ShellOption(help = "Genre titles (with space as delimiter)", value = {"--genre", "--g"}) String[] genreTitles) {
        BookDto book = new BookDto(bookId, bookTitle, new AuthorDto(authorName), Arrays.stream(genreTitles).map(GenreDto::new).collect(Collectors.toList()));
        return bookService.update(book).toString();
    }

    @ShellMethod(value = "Get book by id", key = {"get", "g"})
    public String getBookById(@ShellOption(help = "Book id", value = {"--id", "--i"}) long bookId) {
        Optional<BookDto> book = bookService.getById(bookId);
        return book.map(BookDto::toString).orElse(null);
    }

    @ShellMethod(value = "Get all books", key = {"all", "a"})
    public List<BookDto> getAll() {
        return bookService.getAll();
    }

    @ShellMethod(value = "Delete book by id", key = {"delete", "d"})
    public boolean deleteBookById(@ShellOption(help = "Book id", value = {"--id", "--i"}) long bookId) {
        return bookService.deleteById(bookId);
    }

    @ShellMethod(value = "Count books", key = {"count", "c"})
    public long countBooks() {
        return bookService.count();
    }
}

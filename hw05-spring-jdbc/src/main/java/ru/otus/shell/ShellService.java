package ru.otus.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Genre;
import ru.otus.service.BookService;

import java.util.List;
import java.util.Optional;

@ShellComponent
public class ShellService {

    private final BookService bookService;

    public ShellService(BookService bookService) {
        this.bookService = bookService;
    }

    @ShellMethod(value = "Insert new book", key = {"insert", "i"})
    public String insertBook(@ShellOption(help = "Book title", value = {"--title", "--t"}) String bookTitle,
                             @ShellOption(help = "Author name", value = {"--author", "--a"}) String authorName,
                             @ShellOption(help = "Genre title", value = {"--genre", "--g"}) String genreTitle) {
        Book book = new Book(bookTitle, new Author(authorName), new Genre(genreTitle));
        return bookService.insert(book).toString();
    }

    @ShellMethod(value = "Update book", key = {"update", "u"})
    public String updateBook(@ShellOption(help = "Book id", value = {"--id", "--i"}) long bookId,
                             @ShellOption(help = "Book title", value = {"--title", "--t"}) String bookTitle,
                             @ShellOption(help = "Author name", value = {"--author", "--a"}) String authorName,
                             @ShellOption(help = "Genre title", value = {"--genre", "--g"}) String genreTitle) {
        Book book = new Book(bookId, bookTitle, new Author(authorName), new Genre(genreTitle));
        return bookService.update(book).toString();
    }

    @ShellMethod(value = "Get book by id", key = {"get", "g"})
    public String getBookById(@ShellOption(help = "Book id", value = {"--id", "--i"}) long bookId) {
        Optional<Book> book = bookService.getById(bookId);
        return book.map(Book::toString).orElse(null);
    }

    @ShellMethod(value = "Get all books", key = {"all", "a"})
    public List<Book> getAll() {
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

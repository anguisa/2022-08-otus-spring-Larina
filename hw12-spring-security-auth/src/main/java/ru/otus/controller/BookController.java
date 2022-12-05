package ru.otus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.GenreDto;
import ru.otus.exception.BookNotFoundException;
import ru.otus.service.AuthorService;
import ru.otus.service.BookService;
import ru.otus.service.GenreService;

import java.util.List;

@Controller
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;

    public BookController(BookService bookService,
                          AuthorService authorService,
                          GenreService genreService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @GetMapping("/books")
    public String listBooks(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute("books", books);
        return "list_books";
    }

    @PostMapping("/books/delete")
    public String deleteBook(@RequestParam("id") long id, Model model) {
        bookService.deleteById(id);
        return "redirect:/books";
    }

    @GetMapping("/books/edit")
    public String editBook(@RequestParam("id") long id, Model model) {
        BookDto book = bookService.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        List<AuthorDto> authors = authorService.findAll();
        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("book", book);
        model.addAttribute("authors", authors);
        model.addAttribute("genres", genres);
        return "edit_book";
    }

    @PostMapping("/books/edit")
    public String editBook(@ModelAttribute("book") BookDto book, Model model) {
        bookService.update(book);
        return "redirect:/books";
    }

    @GetMapping("/books/create")
    public String createBook(Model model) {
        List<AuthorDto> authors = authorService.findAll();
        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("book", new BookDto(null, "", new AuthorDto(null), List.of()));
        model.addAttribute("authors", authors);
        model.addAttribute("genres", genres);
        return "edit_book";
    }

    @PostMapping("/books/create")
    public String createBook(@ModelAttribute("book") BookDto book, Model model) {
        bookService.insert(book);
        return "redirect:/books";
    }

    @GetMapping("/error")
    public String errorPage() {
        return "error";
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleNotFound(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}

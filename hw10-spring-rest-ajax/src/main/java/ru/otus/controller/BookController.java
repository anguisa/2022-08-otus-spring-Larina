package ru.otus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.dto.BookDto;
import ru.otus.exception.BookNotFoundException;
import ru.otus.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/api/books")
    public ResponseEntity<List<BookDto>> listBooks() {
        return ResponseEntity.ok().body(bookService.findAll());
    }

    @GetMapping("/api/books/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable("id") long id) {
        BookDto book = bookService.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        return ResponseEntity.ok().body(book);
    }

    @PostMapping("/api/books")
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto book) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.insert(book));
    }

    @PutMapping("/api/books/{id}")
    public ResponseEntity<BookDto> editBook(@PathVariable("id") long id, @RequestBody BookDto book) {
        return ResponseEntity.ok().body(bookService.update(book));
    }

    @DeleteMapping("/api/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable("id") long id) {
        bookService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleNotFound(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}

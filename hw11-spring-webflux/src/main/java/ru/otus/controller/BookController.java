package ru.otus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.dao.BookDao;
import ru.otus.dao.CommentDao;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.dto.BookDto;
import ru.otus.dto.converter.DtoConverter;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class BookController {

    private final BookDao bookDao;
    private final CommentDao commentDao;
    private final DtoConverter<Book, BookDto> bookConverter;

    public BookController(BookDao bookDao,
                          CommentDao commentDao,
                          DtoConverter<Book, BookDto> bookConverter) {
        this.bookDao = bookDao;
        this.commentDao = commentDao;
        this.bookConverter = bookConverter;
    }

    @GetMapping("/api/books")
    public Flux<BookDto> listBooks() {
        return bookDao.findAll()
            .map(bookConverter::toDto);
    }

    @GetMapping("/api/books/{id}")
    public Mono<ResponseEntity<BookDto>> getBookById(@PathVariable("id") String id) {
        return bookDao.findById(id)
            .map(bookConverter::toDto)
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping("/api/books")
    public Mono<ResponseEntity<BookDto>> createBook(@RequestBody BookDto book) {
        return Mono.fromCallable(() -> bookConverter.fromDto(book))
            .flatMap(bookDao::save)
            .map(bookConverter::toDto)
            .map(bookSaved -> ResponseEntity.status(HttpStatus.CREATED).body(bookSaved));
    }

    @PutMapping("/api/books/{id}")
    public Mono<ResponseEntity<BookDto>> editBook(@PathVariable("id") String id, @RequestBody BookDto book) {
        return Mono.fromCallable(() -> bookConverter.fromDto(book.setId(id)))
            .flatMap(bookDao::updateBookWithoutComments)
            .map(bookConverter::toDto)
            .map(ResponseEntity::ok);
    }

    @DeleteMapping("/api/books/{id}")
    public Mono<ResponseEntity<Void>> deleteBook(@PathVariable("id") String id) {
        return bookDao.findById(id)
            .flatMap(book -> {
                List<Comment> comments = (book.getComments() == null) ? List.of() : book.getComments();
                return commentDao.deleteAllById(comments.stream().map(Comment::getId).collect(Collectors.toList()))
                    .then(bookDao.deleteById(id));
            })
            .map(ResponseEntity::ok);
    }

}

package ru.otus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.dao.BookDao;
import ru.otus.dao.CommentDao;
import ru.otus.domain.Comment;
import ru.otus.dto.CommentDto;
import ru.otus.dto.converter.DtoConverter;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class CommentController {

    private final CommentDao commentDao;
    private final BookDao bookDao;
    private final DtoConverter<Comment, CommentDto> commentConverter;

    public CommentController(CommentDao commentDao,
                             BookDao bookDao,
                             DtoConverter<Comment, CommentDto> commentConverter) {
        this.commentDao = commentDao;
        this.bookDao = bookDao;
        this.commentConverter = commentConverter;
    }

    @GetMapping("/api/books/{bookId}/comments")
    public Flux<CommentDto> listComments(@PathVariable("bookId") String bookId) {
        return bookDao.findById(bookId)
            .flatMapIterable(book -> (book.getComments() == null) ? List.of() : book.getComments())
            .map(commentConverter::toDto);
    }

    @GetMapping("/api/books/{bookId}/comments/{id}")
    public Mono<ResponseEntity<CommentDto>> getCommentById(@PathVariable("bookId") String bookId, @PathVariable("id") String id) {
        return commentDao.findById(id)
            .map(commentConverter::toDto)
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping("/api/books/{bookId}/comments")
    public Mono<ResponseEntity<CommentDto>> createComment(@PathVariable("bookId") String bookId, @RequestBody CommentDto commentDto) {
        return bookDao.findById(bookId)
            .flatMap(book ->
                    Mono.fromCallable(() -> commentConverter.fromDto(commentDto))
                        .flatMap(commentDao::save)
                        .flatMap(commentSaved -> {
                            if (book.getComments() == null) {
                                book.setComments(new ArrayList<>());
                            }
                            book.getComments().add(commentSaved);
                            return bookDao.save(book).thenReturn(commentSaved);
                        })
            )
            .map(commentConverter::toDto)
            .map(commentSaved -> ResponseEntity.status(HttpStatus.CREATED).body(commentSaved))
            .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PutMapping("/api/books/{bookId}/comments/{id}")
    public Mono<ResponseEntity<CommentDto>> editComment(@PathVariable("bookId") String bookId, @PathVariable("id") String commentId, @RequestBody CommentDto comment) {
        return Mono.fromCallable(() -> commentConverter.fromDto(comment.setId(commentId)))
            .flatMap(commentDao::save)
            .map(commentConverter::toDto)
            .map(ResponseEntity::ok);
    }

    @DeleteMapping("/api/books/{bookId}/comments/{id}")
    public Mono<ResponseEntity<?>> deleteComment(@PathVariable("bookId") String bookId, @PathVariable("id") String commentId) {
        return commentDao.deleteById(commentId)
            .then(bookDao.deleteCommentByIdAndBookId(commentId, bookId))
            .thenReturn(ResponseEntity.ok().build());
    }

}

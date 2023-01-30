package ru.otus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.dto.CommentDto;
import ru.otus.service.CommentService;

@RestController
@RequestMapping("/")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/api/books/{bookId}/comments")
    public Flux<CommentDto> listComments(@PathVariable("bookId") String bookId) {
        return commentService.listComments(bookId);
    }

    @GetMapping("/api/books/{bookId}/comments/{id}")
    public Mono<ResponseEntity<CommentDto>> getCommentById(@PathVariable("bookId") String bookId, @PathVariable("id") String id) {
        return commentService.getCommentById(bookId, id)
            .map(ResponseEntity::ok)
            .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping("/api/books/{bookId}/comments")
    public Mono<ResponseEntity<CommentDto>> createComment(@PathVariable("bookId") String bookId, @RequestBody CommentDto commentDto) {
        return commentService.createComment(bookId, commentDto)
            .map(commentSaved -> ResponseEntity.status(HttpStatus.CREATED).body(commentSaved))
            .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PutMapping("/api/books/{bookId}/comments/{id}")
    public Mono<ResponseEntity<CommentDto>> editComment(@PathVariable("bookId") String bookId, @PathVariable("id") String commentId, @RequestBody CommentDto comment) {
        return commentService.editComment(bookId, commentId, comment)
            .map(ResponseEntity::ok);
    }

    @DeleteMapping("/api/books/{bookId}/comments/{id}")
    public Mono<ResponseEntity<?>> deleteComment(@PathVariable("bookId") String bookId, @PathVariable("id") String commentId) {
        return commentService.deleteComment(bookId, commentId)
            .thenReturn(ResponseEntity.ok().build());
    }

}

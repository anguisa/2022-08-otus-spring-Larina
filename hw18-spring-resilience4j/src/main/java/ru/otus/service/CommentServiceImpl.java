package ru.otus.service;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.dao.BookDao;
import ru.otus.dao.CommentDao;
import ru.otus.domain.Comment;
import ru.otus.dto.CommentDto;
import ru.otus.dto.converter.DtoConverter;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final BookDao bookDao;
    private final DtoConverter<Comment, CommentDto> commentConverter;

    public CommentServiceImpl(CommentDao commentDao,
                              BookDao bookDao,
                              DtoConverter<Comment, CommentDto> commentConverter) {
        this.commentDao = commentDao;
        this.bookDao = bookDao;
        this.commentConverter = commentConverter;
    }

    @Override
    @CircuitBreaker(name = "mongoDb", fallbackMethod = "listCommentsFallback")
    @TimeLimiter(name = "mongoDb")
    @Retry(name = "mongoDb")
    public Flux<CommentDto> listComments(String bookId) {
        return bookDao.findById(bookId)
            .flatMapIterable(book -> (book.getComments() == null) ? List.of() : book.getComments())
            .map(commentConverter::toDto);
    }

    @Override
    @CircuitBreaker(name = "mongoDb", fallbackMethod = "getCommentByIdFallback")
    @TimeLimiter(name = "mongoDb")
    @Retry(name = "mongoDb")
    public Mono<CommentDto> getCommentById(String bookId, String id) {
        return commentDao.findById(id)
            .map(commentConverter::toDto);
    }

    @Override
    @CircuitBreaker(name = "mongoDb")
    @TimeLimiter(name = "mongoDb")
    @Bulkhead(name = "mongoDb")
    public Mono<CommentDto> createComment(String bookId, CommentDto commentDto) {
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
            .map(commentConverter::toDto);
    }

    @Override
    @CircuitBreaker(name = "mongoDb")
    @TimeLimiter(name = "mongoDb")
    @Bulkhead(name = "mongoDb")
    public Mono<CommentDto> editComment(String bookId, String commentId, CommentDto comment) {
        return Mono.fromCallable(() -> commentConverter.fromDto(comment.setId(commentId)))
            .flatMap(commentDao::save)
            .map(commentConverter::toDto);
    }

    @Override
    @CircuitBreaker(name = "mongoDb")
    @TimeLimiter(name = "mongoDb")
    @Bulkhead(name = "mongoDb")
    public Mono<Void> deleteComment(String bookId, String commentId) {
        return commentDao.deleteById(commentId)
            .then(bookDao.deleteCommentByIdAndBookId(commentId, bookId));
    }

    private Flux<CommentDto> listCommentsFallback(Throwable t) {
        return Flux.just(new CommentDto("00000", "Без текста"));
    }

    private Mono<CommentDto> getCommentByIdFallback(Throwable t) {
        return Mono.just(new CommentDto("00000", "Без текста"));
    }

}

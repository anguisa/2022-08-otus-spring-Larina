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
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.GenreDto;
import ru.otus.dto.converter.DtoConverter;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;
    private final CommentDao commentDao;
    private final DtoConverter<Book, BookDto> bookConverter;

    public BookServiceImpl(BookDao bookDao,
                           CommentDao commentDao,
                           DtoConverter<Book, BookDto> bookConverter) {
        this.bookDao = bookDao;
        this.commentDao = commentDao;
        this.bookConverter = bookConverter;
    }

    @Override
    @CircuitBreaker(name = "mongoDb", fallbackMethod = "listBooksFallback")
    @TimeLimiter(name = "mongoDb")
    @Retry(name = "mongoDb")
    public Flux<BookDto> listBooks() {
        return bookDao.findAll()
            .map(bookConverter::toDto)
//            .delayElements(Duration.of(5, TimeUnit.SECONDS.toChronoUnit())) // для проверки
            ;
    }

    @Override
    @CircuitBreaker(name = "mongoDb", fallbackMethod = "getBookByIdFallback")
    @TimeLimiter(name = "mongoDb")
    @Retry(name = "mongoDb")
    public Mono<BookDto> getBookById(String id) {
        return bookDao.findById(id)
            .map(bookConverter::toDto);
    }

    @Override
    @CircuitBreaker(name = "mongoDb")
    @TimeLimiter(name = "mongoDb")
    @Bulkhead(name = "mongoDb")
    public Mono<BookDto> createBook(BookDto book) {
        return Mono.fromCallable(() -> bookConverter.fromDto(book))
            .flatMap(bookDao::save)
            .map(bookConverter::toDto);
    }

    @Override
    @CircuitBreaker(name = "mongoDb")
    @TimeLimiter(name = "mongoDb")
    @Bulkhead(name = "mongoDb")
    public Mono<BookDto> editBook(String id, BookDto book) {
        return Mono.fromCallable(() -> bookConverter.fromDto(book.setId(id)))
            .flatMap(bookDao::updateBookWithoutComments)
            .map(bookConverter::toDto);
    }

    @Override
    @CircuitBreaker(name = "mongoDb")
    @TimeLimiter(name = "mongoDb")
    @Bulkhead(name = "mongoDb")
    public Mono<Void> deleteBook(String id) {
        return bookDao.findById(id)
            .flatMap(book -> {
                List<Comment> comments = (book.getComments() == null) ? List.of() : book.getComments();
                return commentDao.deleteAllById(comments.stream().map(Comment::getId).collect(Collectors.toList()))
                    .then(bookDao.deleteById(id));
            });
    }

    private Flux<BookDto> listBooksFallback(Throwable t) {
        return Flux.just(new BookDto("0", "Без названия", new AuthorDto("00", "Без имени"), List.of(new GenreDto("000", "Без названия"))));
    }

    private Mono<BookDto> getBookByIdFallback(Throwable t) {
        return Mono.just(new BookDto("0", "Без названия", new AuthorDto("00", "Без имени"), List.of(new GenreDto("000", "Без названия"))));
    }

}

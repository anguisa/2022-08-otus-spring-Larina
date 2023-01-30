package ru.otus.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.dto.BookDto;

public interface BookService {
    Flux<BookDto> listBooks();

    Mono<BookDto> getBookById(String id);

    Mono<BookDto> createBook(@RequestBody BookDto book);

    Mono<BookDto> editBook(@PathVariable("id") String id, @RequestBody BookDto book);

    Mono<Void> deleteBook(@PathVariable("id") String id);
}

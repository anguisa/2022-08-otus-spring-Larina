package ru.otus.service;

import ru.otus.dto.BookDto;

import java.util.List;
import java.util.Optional;

public interface BookService {

    BookDto insert(BookDto book);

    BookDto update(BookDto book);

    void deleteById(String id);

    Optional<BookDto> findById(String id);

    List<BookDto> findAll();

    long count();
}

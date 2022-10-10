package ru.otus.service;

import ru.otus.dto.BookDto;

import java.util.List;
import java.util.Optional;

public interface BookService {

    BookDto insert(BookDto book);

    BookDto update(BookDto book);

    boolean deleteById(long id);

    Optional<BookDto> getById(long id);

    List<BookDto> getAll();

    long count();
}

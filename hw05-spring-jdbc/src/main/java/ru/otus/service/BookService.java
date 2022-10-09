package ru.otus.service;

import ru.otus.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    Book insert(Book book);

    Optional<Book> update(Book book);

    boolean deleteById(long id);

    Optional<Book> getById(long id);

    List<Book> getAll();

    long count();
}

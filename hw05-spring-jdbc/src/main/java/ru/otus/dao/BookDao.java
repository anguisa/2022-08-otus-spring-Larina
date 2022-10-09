package ru.otus.dao;

import ru.otus.domain.Book;

import java.util.List;
import java.util.Optional;

public interface BookDao {

    long count();

    long insert(Book book);

    boolean update(Book book);

    boolean deleteById(long id);

    Optional<Book> getById(long id);

    List<Book> getAll();
}

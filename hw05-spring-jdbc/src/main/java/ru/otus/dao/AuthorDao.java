package ru.otus.dao;

import ru.otus.domain.Author;

import java.util.Optional;

public interface AuthorDao {
    long insert(Author author);

    Optional<Author> getById(long id);

    Optional<Author> getByName(String name);
}

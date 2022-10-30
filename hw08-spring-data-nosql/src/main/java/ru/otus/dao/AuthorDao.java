package ru.otus.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.domain.Author;

import java.util.Optional;

public interface AuthorDao extends MongoRepository<Author, String> {

    Optional<Author> findByName(String name);
}

package ru.otus.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.otus.domain.Author;

public interface AuthorDao extends ReactiveMongoRepository<Author, String> {
}

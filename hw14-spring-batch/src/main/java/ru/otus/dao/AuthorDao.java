package ru.otus.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.domain.mongo.Author;

public interface AuthorDao extends MongoRepository<Author, String> {
}

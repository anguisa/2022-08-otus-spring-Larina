package ru.otus.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.domain.mongo.Book;

public interface BookDao extends MongoRepository<Book, String> {
}

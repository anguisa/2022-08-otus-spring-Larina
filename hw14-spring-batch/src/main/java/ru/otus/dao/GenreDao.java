package ru.otus.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.domain.mongo.Genre;

public interface GenreDao extends MongoRepository<Genre, String> {
}

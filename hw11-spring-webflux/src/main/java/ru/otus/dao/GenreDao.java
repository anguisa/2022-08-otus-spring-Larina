package ru.otus.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.otus.domain.Genre;

public interface GenreDao extends ReactiveMongoRepository<Genre, String> {
}

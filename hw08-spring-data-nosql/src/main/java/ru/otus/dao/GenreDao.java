package ru.otus.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.domain.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao extends MongoRepository<Genre, String> {

    List<Genre> findByTitleIn(List<String> titles);
}

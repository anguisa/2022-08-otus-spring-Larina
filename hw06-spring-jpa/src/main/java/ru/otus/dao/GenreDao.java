package ru.otus.dao;

import ru.otus.domain.Genre;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GenreDao {
    Genre save(Genre genre);

    Optional<Genre> getById(long id);

    Optional<Genre> getByTitle(String title);

    Map<String, Genre> getByTitles(List<String> titles);
}

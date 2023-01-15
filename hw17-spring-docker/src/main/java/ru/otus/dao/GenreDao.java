package ru.otus.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.domain.Genre;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "genres")
public interface GenreDao extends JpaRepository<Genre, Long> {

    @RestResource(path = "title", rel = "title")
    Optional<Genre> findByTitle(String title);

    @RestResource(path = "titles", rel = "titles")
    List<Genre> findByTitleIn(List<String> titles);
}

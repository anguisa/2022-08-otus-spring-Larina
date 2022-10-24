package ru.otus.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.domain.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDao extends JpaRepository<Genre, Long> {

    Optional<Genre> findByTitle(String title);

    List<Genre> findByTitleIn(List<String> titles);
}

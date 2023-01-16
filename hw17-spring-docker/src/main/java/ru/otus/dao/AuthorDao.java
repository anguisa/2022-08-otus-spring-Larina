package ru.otus.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import ru.otus.domain.Author;

import java.util.Optional;

@RepositoryRestResource(path = "authors")
public interface AuthorDao extends JpaRepository<Author, Long> {

    @RestResource(path = "name", rel = "name") // параметр для search
    Optional<Author> findByName(String name);
}

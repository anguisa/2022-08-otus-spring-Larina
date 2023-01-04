package ru.otus.dao;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.domain.Book;

import java.util.List;

import static ru.otus.domain.Book.GRAPH_BOOK_AUTHOR;

@RepositoryRestResource(path = "books")
public interface BookDao extends JpaRepository<Book, Long> {

    @Override
    @EntityGraph(value = GRAPH_BOOK_AUTHOR)
    @Query("select distinct e from Book e left join fetch e.genres g")
    List<Book> findAll();
}

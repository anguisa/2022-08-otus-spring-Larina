package ru.otus.dao;

import org.springframework.stereotype.Component;
import ru.otus.domain.Book;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Component
public class BookDaoJpa implements BookDao {

    @PersistenceContext
    private final EntityManager em;

    public BookDaoJpa(EntityManager em) {
        this.em = em;
    }

    @Override
    public long count() {
        return em.createQuery("select count(b) from Book b", Long.class).getSingleResult();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == null) {
            em.persist(book);
            return book;
        }

        return em.merge(book);
    }

    @Override
    public boolean deleteById(long id) {
        return getById(id)
            .map(book -> {
                em.remove(book);
                return true;
            }).orElse(false);
    }

    @Override
    public Optional<Book> getById(long id) {
        return Optional.ofNullable(em.find(Book.class, id));
    }

    @Override
    public List<Book> getAll() {
        EntityGraph<?> entityGraph = em.getEntityGraph(Book.GRAPH_BOOK_AUTHOR);
        TypedQuery<Book> query = em.createQuery("select distinct e from Book e left join fetch e.genres g", Book.class);
        query.setHint(FETCH.getKey(), entityGraph);
        return query.getResultList();
    }
}

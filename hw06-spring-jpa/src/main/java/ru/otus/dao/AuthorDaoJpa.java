package ru.otus.dao;

import org.springframework.stereotype.Component;
import ru.otus.domain.Author;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Component
public class AuthorDaoJpa implements AuthorDao {

    @PersistenceContext
    private final EntityManager em;

    public AuthorDaoJpa(EntityManager em) {
        this.em = em;
    }

    @Override
    public Author save(Author author) {
        if (author.getId() == null) {
            em.persist(author);
            return author;
        }

        return em.merge(author);
    }

    @Override
    public Optional<Author> getById(long id) {
        return Optional.ofNullable(em.find(Author.class, id));
    }

    @Override
    public Optional<Author> getByName(String name) {
        TypedQuery<Author> query = em.createQuery("select e from Author e where e.name = :name", Author.class);
        query.setParameter("name", name);
        List<Author> resultList = query.getResultList();
        return (resultList.isEmpty()) ? Optional.empty() : Optional.of(resultList.get(0));
    }
}

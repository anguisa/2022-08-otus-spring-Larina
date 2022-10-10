package ru.otus.dao;

import org.springframework.stereotype.Repository;
import ru.otus.domain.Comment;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class CommentDaoJpa implements CommentDao {

    @PersistenceContext
    private final EntityManager em;

    public CommentDaoJpa(EntityManager em) {
        this.em = em;
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            em.persist(comment);
            return comment;
        }

        return em.merge(comment);
    }

    @Override
    public boolean deleteById(long id) {
        return getById(id)
            .map(comment -> {
                em.remove(comment);
                return true;
            }).orElse(false);
    }

    @Override
    public Optional<Comment> getById(long id) {
        return Optional.ofNullable(em.find(Comment.class, id));
    }

    @Override
    public List<Comment> getByBookId(long bookId) {
        TypedQuery<Comment> query = em.createQuery("select e from Comment e join fetch e.book where e.book.id = :bookId", Comment.class);
        query.setParameter("bookId", bookId);
        return query.getResultList();
    }
}

package ru.otus.dao;

import org.springframework.stereotype.Component;
import ru.otus.domain.Genre;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GenreDaoJpa implements GenreDao {

    @PersistenceContext
    private final EntityManager em;

    public GenreDaoJpa(EntityManager em) {
        this.em = em;
    }

    @Override
    public Genre save(Genre genre) {
        if (genre.getId() == null) {
            em.persist(genre);
            return genre;
        }

        return em.merge(genre);
    }

    @Override
    public Optional<Genre> getById(long id) {
        return Optional.ofNullable(em.find(Genre.class, id));
    }

    @Override
    public Optional<Genre> getByTitle(String title) {
        TypedQuery<Genre> query = em.createQuery("select e from Genre e where e.title = :title ", Genre.class);
        query.setParameter("title", title);
        List<Genre> resultList = query.getResultList();
        return (resultList.isEmpty()) ? Optional.empty() : Optional.of(resultList.get(0));
    }

    @Override
    public Map<String, Genre> getByTitles(List<String> titles) {
        TypedQuery<Genre> query = em.createQuery("select e from Genre e where e.title in :titles ", Genre.class);
        query.setParameter("titles", titles);
        return query.getResultList().stream().collect(Collectors.toMap(Genre::getTitle, Function.identity()));
    }

}

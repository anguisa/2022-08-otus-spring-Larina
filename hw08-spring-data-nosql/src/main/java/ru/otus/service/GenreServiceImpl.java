package ru.otus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dao.GenreDao;
import ru.otus.domain.Genre;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreDao genreDao;

    public GenreServiceImpl(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @Transactional
    @Override
    public List<Genre> findByTitleInOrCreate(List<String> titles) {
        List<Genre> genres = new ArrayList<>();
        Map<String, Genre> genresFound = genreDao.findByTitleIn(titles).stream().collect(Collectors.toMap(Genre::getTitle, Function.identity()));
        titles.forEach(title -> {
            Genre genre = genresFound.get(title);
            if (genre == null) {
                genre = genreDao.save(new Genre(title));
            }
            genres.add(genre);
        });
        return genres;
    }
}

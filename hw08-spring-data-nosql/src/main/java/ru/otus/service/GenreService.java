package ru.otus.service;

import ru.otus.domain.Genre;

import java.util.List;

public interface GenreService {

    List<Genre> findByTitleInOrCreate(List<String> titles);

}

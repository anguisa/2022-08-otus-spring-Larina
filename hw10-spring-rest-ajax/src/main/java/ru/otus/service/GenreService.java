package ru.otus.service;

import ru.otus.dto.GenreDto;

import java.util.List;

public interface GenreService {

    List<GenreDto> findAll();

}

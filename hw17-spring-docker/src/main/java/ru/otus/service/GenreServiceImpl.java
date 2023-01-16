package ru.otus.service;

import org.springframework.stereotype.Service;
import ru.otus.dao.GenreDao;
import ru.otus.domain.Genre;
import ru.otus.dto.GenreDto;
import ru.otus.dto.converter.DtoConverter;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreDao genreDao;
    private final DtoConverter<Genre, GenreDto> genreConverter;

    public GenreServiceImpl(GenreDao genreDao, DtoConverter<Genre, GenreDto> genreConverter) {
        this.genreDao = genreDao;
        this.genreConverter = genreConverter;
    }

    @Override
    public List<GenreDto> findAll() {
        return genreDao.findAll().stream().map(genreConverter::toDto).collect(Collectors.toList());
    }

}

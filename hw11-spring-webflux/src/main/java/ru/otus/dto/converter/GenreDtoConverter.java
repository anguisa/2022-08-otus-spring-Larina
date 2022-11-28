package ru.otus.dto.converter;

import org.springframework.stereotype.Component;
import ru.otus.domain.Genre;
import ru.otus.dto.GenreDto;

@Component
public class GenreDtoConverter implements DtoConverter<Genre, GenreDto> {

    @Override
    public GenreDto toDto(Genre entity) {
        return new GenreDto(entity.getId(), entity.getTitle());
    }

    @Override
    public Genre fromDto(GenreDto dto) {
        return new Genre(dto.getId(), dto.getTitle());
    }
}

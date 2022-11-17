package ru.otus.dto.formatter;

import org.springframework.format.Formatter;
import ru.otus.dto.GenreDto;

import java.text.ParseException;
import java.util.Locale;

public class GenreFormatter implements Formatter<GenreDto> {
    @Override
    public GenreDto parse(String id, Locale locale) throws ParseException {
        return new GenreDto(Long.parseLong(id));
    }

    @Override
    public String print(GenreDto genre, Locale locale) {
        return (genre.getId() == null) ? "" : genre.getId().toString();
    }
}

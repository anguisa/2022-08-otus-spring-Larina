package ru.otus.dto.converter;

import org.springframework.stereotype.Component;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Genre;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookDto;
import ru.otus.dto.GenreDto;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class BookDtoConverter implements DtoConverter<Book, BookDto> {

    private final DtoConverter<Author, AuthorDto> authorConverter;
    private final DtoConverter<Genre, GenreDto> genreConverter;

    public BookDtoConverter(DtoConverter<Author, AuthorDto> authorConverter,
                            DtoConverter<Genre, GenreDto> genreConverter) {
        this.authorConverter = authorConverter;
        this.genreConverter = genreConverter;
    }

    @Override
    public BookDto toDto(Book entity) {
        return new BookDto(
            entity.getId(),
            entity.getTitle(),
            authorConverter.toDto(entity.getAuthor()),
            (entity.getGenres() == null) ? new ArrayList<>() : entity.getGenres().stream().map(genreConverter::toDto).collect(Collectors.toList())
        );
    }

    @Override
    public Book fromDto(BookDto dto) {
        return new Book(
            dto.getId(),
            dto.getTitle(),
            (dto.getAuthor() == null) ? null : authorConverter.fromDto(dto.getAuthor()),
            (dto.getGenres() == null) ? new ArrayList<>() : dto.getGenres().stream().map(genreConverter::fromDto).collect(Collectors.toList())
        );
    }

}
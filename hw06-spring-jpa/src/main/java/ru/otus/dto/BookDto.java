package ru.otus.dto;

import ru.otus.domain.Book;

import java.util.List;
import java.util.stream.Collectors;

public class BookDto {
    private Long id;
    private final String title;
    private final AuthorDto author;
    private final List<GenreDto> genres;

    public BookDto(Book delegate) {
        this(
            delegate.getId(),
            delegate.getTitle(),
            new AuthorDto(delegate.getAuthor()),
            (delegate.getGenres() == null) ? null : delegate.getGenres().stream().map(GenreDto::new).collect(Collectors.toList())
        );
    }

    public BookDto(Long id) {
        this(id, null, null, null);
    }

    public BookDto(Long id, String title, AuthorDto author, List<GenreDto> genres) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genres = genres;
    }

    public BookDto(String title, AuthorDto author, List<GenreDto> genres) {
        this(null, title, author, genres);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BookDto setId(Long id) {
        this.id = id;
        return this;
    }

    public AuthorDto getAuthor() {
        return author;
    }

    public List<GenreDto> getGenres() {
        return genres;
    }

    @Override
    public String toString() {
        return "Book{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", author=" + author +
            ", genres=" + genres +
            '}';
    }

    public Book fromDto() {
        return new Book(
            id,
            title,
            (author == null) ? null : author.fromDto(),
            (genres == null) ? null : genres.stream().map(GenreDto::fromDto).collect(Collectors.toList())
        );
    }
}

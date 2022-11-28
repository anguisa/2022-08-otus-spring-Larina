package ru.otus.dto;

import java.util.List;

public class LibraryDto {
    private List<AuthorDto> authors;
    private List<GenreDto> genres;

    public LibraryDto(List<AuthorDto> authors, List<GenreDto> genres) {
        this.authors = authors;
        this.genres = genres;
    }

    public List<AuthorDto> getAuthors() {
        return authors;
    }

    public LibraryDto setAuthors(List<AuthorDto> authors) {
        this.authors = authors;
        return this;
    }

    public List<GenreDto> getGenres() {
        return genres;
    }

    public LibraryDto setGenres(List<GenreDto> genres) {
        this.genres = genres;
        return this;
    }
}

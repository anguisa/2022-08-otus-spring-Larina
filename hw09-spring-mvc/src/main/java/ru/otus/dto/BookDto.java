package ru.otus.dto;

import java.util.List;

public class BookDto {

    private Long id;
    private final String title;
    private final AuthorDto author;
    private final List<GenreDto> genres;

    public BookDto(Long id, String title, AuthorDto author, List<GenreDto> genres) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genres = genres;
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

}

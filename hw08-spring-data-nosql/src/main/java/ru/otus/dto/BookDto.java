package ru.otus.dto;

import java.util.ArrayList;
import java.util.List;

public class BookDto {
    private String id;
    private final String title;
    private final AuthorDto author;
    private final List<GenreDto> genres;

    public BookDto(String id) {
        this(id, "", null, new ArrayList<>());
    }

    public BookDto(String id, String title, AuthorDto author, List<GenreDto> genres) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genres = genres;
    }

    public BookDto(String title, AuthorDto author, List<GenreDto> genres) {
        this(null, title, author, genres);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BookDto setId(String id) {
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

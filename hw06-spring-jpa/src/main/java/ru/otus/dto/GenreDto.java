package ru.otus.dto;

import ru.otus.domain.Genre;

public class GenreDto {
    private Long id;
    private final String title;

    public GenreDto(Genre delegate) {
        this(delegate.getId(), delegate.getTitle());
    }

    public GenreDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public GenreDto(String title) {
        this(null, title);
    }

    public Long getId() {
        return id;
    }

    public GenreDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Genre{" +
            "id=" + id +
            ", title='" + title + '\'' +
            '}';
    }

    public Genre fromDto() {
        return new Genre(id, title);
    }
}

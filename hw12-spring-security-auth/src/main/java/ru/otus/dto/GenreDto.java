package ru.otus.dto;

import java.util.Objects;

public class GenreDto {
    private Long id;
    private final String title;

    public GenreDto(Long id) {
        this(id, null);
    }

    public GenreDto(Long id, String title) {
        this.id = id;
        this.title = title;
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

    // необходим для contains в шаблоне редактирования книги (чтобы определить в select.option соответствующие жанры)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenreDto genreDto = (GenreDto) o;
        return Objects.equals(id, genreDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

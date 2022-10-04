package ru.otus.domain;

import java.util.Objects;

public class Genre {

    private Long id;
    private final String title;

    public Genre(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Genre(String title) {
        this(null, title);
    }

    public Genre(Long id) {
        this(id, null);
    }

    public Long getId() {
        return id;
    }

    public Genre setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Genre genre = (Genre) o;
        return Objects.equals(id, genre.id) && Objects.equals(title, genre.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    @Override
    public String toString() {
        return "Genre{" +
            "id=" + id +
            ", title='" + title + '\'' +
            '}';
    }
}

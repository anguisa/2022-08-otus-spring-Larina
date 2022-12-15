package ru.otus.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "genre")
public class Genre {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    public Genre() {
    }

    public Genre(Long id, String title) {
        this.id = id;
        this.title = title;
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

    public Genre setTitle(String title) {
        this.title = title;
        return this;
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
}

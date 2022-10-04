package ru.otus.domain;

import java.util.Objects;

public class Book {

    private Long id;
    private final String title;
    private final Author author;
    private final Genre genre;

    public Book(Long id, String title, Author author, Genre genre) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public Book(String title, Author author, Genre genre) {
        this(null, title, author, genre);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Book setId(Long id) {
        this.id = id;
        return this;
    }

    public Author getAuthor() {
        return author;
    }

    public Genre getGenre() {
        return genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id) && Objects.equals(title, book.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    @Override
    public String toString() {
        return "Book{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", author=" + author +
            ", genre=" + genre +
            '}';
    }
}

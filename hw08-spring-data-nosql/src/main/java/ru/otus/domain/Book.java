package ru.otus.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = "books")
public class Book {

    @Id
    private String id;

    @NotNull
    @NotEmpty
    @Field(name = "title")
    private String title;

    @NotNull
    @Field(name = "author")
    @DBRef
    private Author author;

    @NotNull
    @Field(name = "genres")
    @DBRef
    private List<Genre> genres = new ArrayList<>();

    @Field(name = "comments")
    @DBRef(lazy = true)
    private List<Comment> comments = new ArrayList<>();

    public Book() {
    }

    public Book(String title, Author author, List<Genre> genres) {
        this(null, title, author, genres);
    }

    public Book(String id, String title, Author author, List<Genre> genres) {
        this(id, title, author, genres, new ArrayList<>());
    }

    public Book(String title, Author author, List<Genre> genres, List<Comment> comments) {
        this(null, title, author, genres, comments);
    }

    public Book(String id, String title, Author author, List<Genre> genres, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genres = genres;
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Book setId(String id) {
        this.id = id;
        return this;
    }

    public Author getAuthor() {
        return author;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public Book setTitle(String title) {
        this.title = title;
        return this;
    }

    public Book setAuthor(Author author) {
        this.author = author;
        return this;
    }

    public Book setGenres(List<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Book setComments(List<Comment> comments) {
        this.comments = comments;
        return this;
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
}

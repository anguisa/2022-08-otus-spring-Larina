package ru.otus.domain.jpa;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "book")
public class BookJpa {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER) // остальные cascade не используем, т.к. иначе при сохранении книги будет затираться другая информация
    @JoinColumn(name = "author_id")
    private AuthorJpa author;

    @ManyToMany(targetEntity = GenreJpa.class, fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH })
    @JoinTable(name = "book_genre", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<GenreJpa> genres;

    @OneToMany(targetEntity = CommentJpa.class, cascade = { CascadeType.ALL }) // все cascade, чтобы сохранить комментарии
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private List<CommentJpa> comments;

    public BookJpa() {
    }

    public BookJpa(String id, String title, AuthorJpa author, List<GenreJpa> genres, List<CommentJpa> comments) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genres = genres;
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public BookJpa setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public BookJpa setTitle(String title) {
        this.title = title;
        return this;
    }

    public AuthorJpa getAuthor() {
        return author;
    }

    public BookJpa setAuthor(AuthorJpa author) {
        this.author = author;
        return this;
    }

    public List<GenreJpa> getGenres() {
        return genres;
    }

    public BookJpa setGenres(List<GenreJpa> genres) {
        this.genres = genres;
        return this;
    }

    public List<CommentJpa> getComments() {
        return comments;
    }

    public BookJpa setComments(List<CommentJpa> comments) {
        this.comments = comments;
        return this;
    }
}

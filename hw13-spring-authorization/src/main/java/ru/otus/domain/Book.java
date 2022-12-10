package ru.otus.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static ru.otus.domain.Book.GRAPH_BOOK_AUTHOR;

@Entity
@Table(name = "book")
@NamedEntityGraph(name = GRAPH_BOOK_AUTHOR, attributeNodes = @NamedAttributeNode("author"))
public class Book {

    public static final String GRAPH_BOOK_AUTHOR = "book_author_graph";

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER) // остальные cascade не используем, т.к. иначе при сохранении книги будет затираться другая имя автора и т.д.
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToMany(targetEntity = Genre.class, fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH })
    @JoinTable(name = "book_genre", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<Genre> genres;

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(targetEntity = Comment.class, mappedBy = "book", cascade = { CascadeType.REFRESH })
    private List<Comment> comments;

    public Book() {
    }

    public Book(Long id, String title, Author author, List<Genre> genres, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genres = genres;
        this.comments = comments;
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

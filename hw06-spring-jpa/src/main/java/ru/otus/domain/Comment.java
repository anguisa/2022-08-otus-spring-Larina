package ru.otus.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "book_comment")
public class Comment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment_text", nullable = false)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    public Comment() {
    }

    public Comment(Long id, String text) {
        this(id, text, null);
    }

    public Comment(Long id, String text, Book book) {
        this.id = id;
        this.text = text;
        this.book = book;
    }

    public Long getId() {
        return id;
    }

    public Comment setId(Long id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public Comment setText(String text) {
        this.text = text;
        return this;
    }

    public Book getBook() {
        return book;
    }

    public Comment setBook(Book book) {
        this.book = book;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

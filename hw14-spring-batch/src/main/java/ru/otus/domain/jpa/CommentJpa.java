package ru.otus.domain.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "book_comment")
public class CommentJpa {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "comment_text")
    private String text;

    public CommentJpa() {
    }

    public CommentJpa(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public CommentJpa setId(String id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public CommentJpa setText(String text) {
        this.text = text;
        return this;
    }
}

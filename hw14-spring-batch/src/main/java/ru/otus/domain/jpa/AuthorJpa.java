package ru.otus.domain.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "author")
public class AuthorJpa {

    @Id
    @Column(name = "id")
    private String id;

    public AuthorJpa() {
    }

    public AuthorJpa(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public AuthorJpa setId(String id) {
        this.id = id;
        return this;
    }
}

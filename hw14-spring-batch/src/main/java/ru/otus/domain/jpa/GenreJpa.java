package ru.otus.domain.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "genre")
public class GenreJpa {

    @Id
    @Column(name = "id")
    private String id;

    public GenreJpa() {
    }

    public GenreJpa(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public GenreJpa setId(String id) {
        this.id = id;
        return this;
    }
}

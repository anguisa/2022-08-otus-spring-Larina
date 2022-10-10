package ru.otus.dto;

import ru.otus.domain.Author;

public class AuthorDto {

    private Long id;
    private final String name;

    public AuthorDto(Author delegate) {
        this(delegate.getId(), delegate.getName());
    }

    public AuthorDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public AuthorDto(String name) {
        this(null, name);
    }

    public Long getId() {
        return id;
    }

    public AuthorDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Author{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }

    public Author fromDto() {
        return new Author(id, name);
    }
}

package ru.otus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorDto {
    private Long id;
    private final String name;

    public AuthorDto(@JsonProperty("id") Long id,
                     @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
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

}

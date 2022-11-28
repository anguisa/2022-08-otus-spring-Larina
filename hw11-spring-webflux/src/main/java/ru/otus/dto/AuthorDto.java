package ru.otus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorDto {
    private String id;
    private final String name;

    public AuthorDto(@JsonProperty("id") String id,
                     @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public AuthorDto setId(String id) {
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

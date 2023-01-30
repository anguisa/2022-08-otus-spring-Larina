package ru.otus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenreDto {
    private String id;
    private final String title;

    public GenreDto(@JsonProperty("id") String id, @JsonProperty("title") String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public GenreDto setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Genre{" +
            "id=" + id +
            ", title='" + title + '\'' +
            '}';
    }
}

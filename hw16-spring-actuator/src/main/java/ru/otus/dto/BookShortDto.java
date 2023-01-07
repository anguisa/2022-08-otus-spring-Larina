package ru.otus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookShortDto {

    private final Long id;

    public BookShortDto(@JsonProperty("id") Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Book{" +
            "id=" + id +
            '}';
    }

}

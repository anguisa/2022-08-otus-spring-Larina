package ru.otus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentDto {
    private Long id;
    private final String text;
    private final BookShortDto book;

    public CommentDto(@JsonProperty("id") Long id,
                      @JsonProperty("text") String text,
                      @JsonProperty("book") BookShortDto book) {
        this.id = id;
        this.text = text;
        this.book = book;
    }

    public Long getId() {
        return id;
    }

    public CommentDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public BookShortDto getBook() {
        return book;
    }

    @Override
    public String toString() {
        return "Comment{" +
            "id=" + id +
            ", text='" + text + '\'' +
            ", book=" + book +
            '}';
    }
}

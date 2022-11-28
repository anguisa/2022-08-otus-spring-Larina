package ru.otus.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentDto {
    private String id;
    private final String text;

    public CommentDto(@JsonProperty("id") String id,
                      @JsonProperty("text") String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public CommentDto setId(String id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Comment{" +
            "id=" + id +
            ", text='" + text + '\'' +
            '}';
    }
}

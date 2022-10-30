package ru.otus.dto;

public class CommentDto {
    private String id;
    private final String text;

    public CommentDto(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public CommentDto(String text) {
        this(null, text);
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

package ru.otus.dto;

public class CommentDto {
    private Long id;
    private final String text;
    private final BookShortDto book;

    public CommentDto(Long id, String text, BookShortDto book) {
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

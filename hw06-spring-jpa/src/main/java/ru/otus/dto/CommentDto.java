package ru.otus.dto;

import ru.otus.domain.Comment;

public class CommentDto {
    private Long id;
    private final String text;
    private final BookShortDto book;

    public CommentDto(Comment delegate) {
        this(
            delegate.getId(),
            delegate.getText(),
            new BookShortDto(delegate.getBook())
        );
    }

    public CommentDto(Long id, String text, BookShortDto book) {
        this.id = id;
        this.text = text;
        this.book = book;
    }

    public CommentDto(String text, BookShortDto book) {
        this(null, text, book);
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

    public Comment fromDto() {
        return new Comment(
            id,
            text,
            (book == null) ? null : book.fromDto()
        );
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

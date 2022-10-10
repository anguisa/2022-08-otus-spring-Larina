package ru.otus.dto;

import ru.otus.domain.Book;

public class BookShortDto {
    private Long id;
    private final String title;

    public BookShortDto(Book delegate) {
        this(delegate.getId(), delegate.getTitle());
    }

    public BookShortDto(Long id) {
        this(id, null);
    }

    public BookShortDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BookShortDto setId(Long id) {
        this.id = id;
        return this;
    }

    @Override
    public String toString() {
        return "Book{" +
            "id=" + id +
            ", title='" + title + '\'' +
            '}';
    }

    public Book fromDto() {
        return new Book(id, title);
    }
}

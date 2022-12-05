package ru.otus.dto;

public class BookShortDto {

    private final Long id;

    public BookShortDto(Long id) {
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

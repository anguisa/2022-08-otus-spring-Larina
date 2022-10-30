package ru.otus.dto;

public class GenreDto {
    private String id;
    private final String title;

    public GenreDto(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public GenreDto(String title) {
        this(null, title);
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

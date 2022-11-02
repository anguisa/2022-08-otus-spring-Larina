package ru.otus.dto;

public class AuthorDto {

    private String id;
    private final String name;

    public AuthorDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public AuthorDto(String name) {
        this(null, name);
    }

    public String getId() {
        return id;
    }

    public AuthorDto setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Author{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }

}

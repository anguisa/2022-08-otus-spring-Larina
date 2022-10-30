package ru.otus.service;

import ru.otus.domain.Author;

public interface AuthorService {

    Author findByNameOrCreate(String name);

}

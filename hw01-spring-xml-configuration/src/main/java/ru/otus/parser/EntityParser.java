package ru.otus.parser;

public interface EntityParser<E, T> {
    E parse(T initialData);
}

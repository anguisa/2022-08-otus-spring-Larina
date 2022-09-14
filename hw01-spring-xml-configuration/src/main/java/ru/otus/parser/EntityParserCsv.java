package ru.otus.parser;

public interface EntityParserCsv<E> extends EntityParser<E, String[]> {
    E parse(String[] line);
}

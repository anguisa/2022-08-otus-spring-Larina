package ru.otus.dto.formatter;

import org.springframework.format.Formatter;
import ru.otus.dto.BookShortDto;

import java.util.Locale;

public class BookShortFormatter implements Formatter<BookShortDto> {

    @Override
    public BookShortDto parse(String id, Locale locale) {
        return new BookShortDto(Long.parseLong(id));
    }

    @Override
    public String print(BookShortDto book, Locale locale) {
        return (book.getId() == null) ? "" : book.getId().toString();
    }
}

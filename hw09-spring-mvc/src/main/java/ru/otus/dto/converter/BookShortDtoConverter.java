package ru.otus.dto.converter;

import org.springframework.stereotype.Component;
import ru.otus.domain.Book;
import ru.otus.dto.BookShortDto;

@Component
public class BookShortDtoConverter implements DtoConverter<Book, BookShortDto> {

    public BookShortDtoConverter() {
    }

    @Override
    public BookShortDto toDto(Book entity) {
        return new BookShortDto(entity.getId());
    }

    @Override
    public Book fromDto(BookShortDto dto) {
        return new Book(dto.getId(), null, null, null, null);
    }

}
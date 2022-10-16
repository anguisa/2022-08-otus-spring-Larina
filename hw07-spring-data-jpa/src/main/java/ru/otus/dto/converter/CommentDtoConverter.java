package ru.otus.dto.converter;

import org.springframework.stereotype.Component;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.dto.BookDto;
import ru.otus.dto.CommentDto;

@Component
public class CommentDtoConverter implements DtoConverter<Comment, CommentDto> {

    private final DtoConverter<Book, BookDto> bookConverter;

    public CommentDtoConverter(DtoConverter<Book, BookDto> bookConverter) {
        this.bookConverter = bookConverter;
    }

    @Override
    public CommentDto toDto(Comment entity) {
        return new CommentDto(
            entity.getId(),
            entity.getText(),
            bookConverter.toDto(entity.getBook())
        );
    }

    @Override
    public Comment fromDto(CommentDto dto) {
        return new Comment(
            dto.getId(),
            dto.getText(),
            bookConverter.fromDto(dto.getBook())
        );
    }

}
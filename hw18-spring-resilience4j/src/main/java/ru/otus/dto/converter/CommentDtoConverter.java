package ru.otus.dto.converter;

import org.springframework.stereotype.Component;
import ru.otus.domain.Comment;
import ru.otus.dto.CommentDto;

@Component
public class CommentDtoConverter implements DtoConverter<Comment, CommentDto> {

    public CommentDtoConverter() {
    }

    @Override
    public CommentDto toDto(Comment entity) {
        return new CommentDto(
            entity.getId(),
            entity.getText()
        );
    }

    @Override
    public Comment fromDto(CommentDto dto) {
        return new Comment(
            dto.getId(),
            dto.getText()
        );
    }

}
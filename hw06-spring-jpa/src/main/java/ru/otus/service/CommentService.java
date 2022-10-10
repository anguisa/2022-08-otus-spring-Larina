package ru.otus.service;

import ru.otus.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    CommentDto insert(CommentDto book);

    CommentDto update(CommentDto book);

    boolean deleteById(long id);

    Optional<CommentDto> getById(long id);

    List<CommentDto> getByBookId(long bookId);

}

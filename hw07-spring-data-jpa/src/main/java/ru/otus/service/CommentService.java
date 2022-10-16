package ru.otus.service;

import ru.otus.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    CommentDto insert(CommentDto book);

    CommentDto update(CommentDto book);

    void deleteById(long id);

    Optional<CommentDto> findById(long id);

    List<CommentDto> findByBookId(long bookId);

}

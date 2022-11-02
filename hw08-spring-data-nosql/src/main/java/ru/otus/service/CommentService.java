package ru.otus.service;

import ru.otus.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    CommentDto insert(CommentDto comment, String bookId);

    CommentDto update(CommentDto comment);

    void deleteByIdAndBookId(String id, String bookId);

    Optional<CommentDto> findById(String id);

    List<CommentDto> findByBookId(String bookId);

}

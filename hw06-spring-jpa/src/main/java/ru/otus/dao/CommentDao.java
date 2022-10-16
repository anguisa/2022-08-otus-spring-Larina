package ru.otus.dao;

import ru.otus.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentDao {

    Comment save(Comment comment);

    boolean deleteById(long id);

    Optional<Comment> getById(long id);

    List<Comment> getByBookId(long bookId);
}

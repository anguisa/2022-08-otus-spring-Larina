package ru.otus.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.domain.Comment;

public interface CommentDao extends MongoRepository<Comment, String> {
}

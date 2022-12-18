package ru.otus.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.domain.mongo.Comment;

public interface CommentDao extends MongoRepository<Comment, String> {
}

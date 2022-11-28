package ru.otus.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.otus.domain.Comment;

public interface CommentDao extends ReactiveMongoRepository<Comment, String> {
}

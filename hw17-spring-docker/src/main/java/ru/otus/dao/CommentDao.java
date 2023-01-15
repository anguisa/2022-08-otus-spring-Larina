package ru.otus.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.domain.Comment;

@RepositoryRestResource(path = "comments")
public interface CommentDao extends JpaRepository<Comment, Long> {
}

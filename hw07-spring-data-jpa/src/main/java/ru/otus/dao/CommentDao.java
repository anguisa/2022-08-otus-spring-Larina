package ru.otus.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.domain.Comment;

import java.util.List;

public interface CommentDao extends JpaRepository<Comment, Long> {
}

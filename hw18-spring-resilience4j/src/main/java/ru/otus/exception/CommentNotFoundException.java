package ru.otus.exception;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(String commentId) {
        super(String.format("Comment %s not found", commentId));
    }

}

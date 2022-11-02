package ru.otus.dao;

public interface BookDaoCustom {

    void deleteCommentByIdAndBookId(String commentId, String bookId);

}

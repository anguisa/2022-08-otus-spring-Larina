package ru.otus.dao;

import reactor.core.publisher.Mono;
import ru.otus.domain.Book;

public interface BookDaoCustom {

    Mono<Void> deleteCommentByIdAndBookId(String commentId, String bookId);

    Mono<Book> updateBookWithoutComments(Book newBook);

}

package ru.otus.dao;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.otus.mongock.changelog.MongoDatabaseChangelog.*;

@DisplayName("Тест DAO для книг")
@DataMongoTest
@EnableMongock
class BookDaoMongoTest {

    @Autowired
    private BookDao bookDao;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @DisplayName("Удаляет комментарий из книги")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldDeleteCommentByIdFromBook() {
        Query query = new Query().addCriteria(Criteria.where("id").is(BOOK_1.getId()));
        Mono<Book> getBook = mongoTemplate.findOne(query, Book.class);
        StepVerifier
            .create(getBook)
            .assertNext(book -> {
                assertNotNull(book);
                compareBooks(BOOK_1, book);
            })
            .expectComplete()
            .verify();

        String commentId = BOOK_1.getComments().get(0).getId();
        Mono<Void> deleteComment = bookDao.deleteCommentByIdAndBookId(commentId, BOOK_1.getId());
        StepVerifier
            .create(deleteComment)
            .expectComplete()
            .verify();

        Mono<Book> getBookAfterDelete = mongoTemplate.findOne(query, Book.class);
        StepVerifier
            .create(getBookAfterDelete)
            .assertNext(book -> {
                assertNotNull(book);
                assertEquals(0, book.getComments().stream().filter(comment -> comment.getId().equals(commentId)).count());
            })
            .expectComplete()
            .verify();
    }

    @DisplayName("Обновляет книгу, не удаляя комментарии")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void shouldUpdateBookWithoutComments() {
        Book bookToUpdate = new Book(BOOK_1.getId(), "Необычная фантастика", new Author(AUTHOR_2.getId(), null),
            List.of(new Genre(GENRE_2.getId(), null)), List.of(COMMENT_3, COMMENT_4));
        Mono<Book> updateBook = bookDao.updateBookWithoutComments(bookToUpdate);

        Book expected = new Book(bookToUpdate.getId(), bookToUpdate.getTitle(), AUTHOR_2, List.of(GENRE_2), BOOK_1.getComments());

        StepVerifier
            .create(updateBook)
            .assertNext(actual -> {
                assertNotNull(actual);
                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
            })
            .expectComplete()
            .verify();

        Mono<Book> getBookAfterUpdate = mongoTemplate.findOne(new Query().addCriteria(Criteria.where("id").is(BOOK_1.getId())), Book.class);
        StepVerifier
            .create(getBookAfterUpdate)
            .assertNext(actual -> {
                assertNotNull(actual);
                assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(expected);
            })
            .expectComplete()
            .verify();
    }

    private void compareBooks(Book actualBook, Book expectedBook) {
        assertThat(actualBook)
            .usingRecursiveComparison()
            .isEqualTo(expectedBook);
    }
}
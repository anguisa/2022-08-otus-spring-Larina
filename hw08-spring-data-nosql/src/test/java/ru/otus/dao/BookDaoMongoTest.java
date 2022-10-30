package ru.otus.dao;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.domain.Book;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.mongock.changelog.MongoDatabaseChangelog.BOOK_1;

@DisplayName("Тест DAO для книг")
@DataMongoTest
@EnableMongock
class BookDaoMongoTest {

    @Autowired
    private BookDao bookDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DisplayName("Удаляет комментарий из книги")
    @Test
    void shouldDeleteCommentByIdFromBook() {
        Query query = new Query().addCriteria(Criteria.where("id").is(BOOK_1.getId()));
        Book expectedBookInitial = mongoTemplate.findOne(query, Book.class);
        assertThat(expectedBookInitial).isNotNull();
        compareBooks(BOOK_1, expectedBookInitial);

        bookDao.deleteCommentByIdAndBookId(BOOK_1.getComments().get(0).getId(), BOOK_1.getId());
        expectedBookInitial.getComments().remove(0);
        Book expectedBook = mongoTemplate.findOne(query, Book.class);
        assertThat(expectedBook).isNotNull();
        compareBooks(expectedBookInitial, expectedBookInitial);

        Optional<Book> actualBook = bookDao.findById(BOOK_1.getId());

        assertThat(actualBook).isNotEmpty();
        compareBooks(actualBook.get(), expectedBook);
    }

    private void compareBooks(Book actualBook, Book expectedBook) {
        assertThat(actualBook)
            .usingRecursiveComparison()
            .isEqualTo(expectedBook);
    }
}
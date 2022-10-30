package ru.otus.dao;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.domain.Author;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.mongock.changelog.MongoDatabaseChangelog.AUTHOR_1;

@DisplayName("Тест DAO для авторов")
@DataMongoTest
@EnableMongock
class AuthorDaoMongoTest {

    @Autowired
    private AuthorDao authorDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DisplayName("Возвращает ожидаемого автора по имени")
    @Test
    void shouldReturnExpectedAuthorByName() {
        Query query = new Query().addCriteria(Criteria.where("name").is(AUTHOR_1.getName()));
        Author expectedAuthor = mongoTemplate.findOne(query, Author.class);
        assertThat(expectedAuthor).isNotNull();
        compareAuthors(AUTHOR_1, expectedAuthor);
        Optional<Author> actualAuthor = authorDao.findByName(expectedAuthor.getName());
        assertThat(actualAuthor).isNotEmpty();
        compareAuthors(actualAuthor.get(), expectedAuthor);
    }

    private void compareAuthors(Author actualAuthor, Author expectedAuthor) {
        assertThat(actualAuthor)
            .usingRecursiveComparison()
            .isEqualTo(expectedAuthor);
    }
}
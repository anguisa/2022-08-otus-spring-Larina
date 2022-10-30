package ru.otus.dao;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.domain.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.mongock.changelog.MongoDatabaseChangelog.GENRE_1;
import static ru.otus.mongock.changelog.MongoDatabaseChangelog.GENRE_2;

@DisplayName("Тест DAO для жанров")
@DataMongoTest
@EnableMongock
class GenreDaoMongoTest {

    @Autowired
    private GenreDao genreDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @DisplayName("Возвращает ожидаемые жанры по списку названий")
    @Test
    void shouldReturnExpectedGenreByTitles() {
        Query query1 = new Query().addCriteria(Criteria.where("title").is(GENRE_1.getTitle()));
        Query query2 = new Query().addCriteria(Criteria.where("title").is(GENRE_2.getTitle()));
        Genre expectedGenre1 = mongoTemplate.findOne(query1, Genre.class);
        assertThat(expectedGenre1).isNotNull();
        compareGenres(GENRE_1, expectedGenre1);
        Genre expectedGenre2 = mongoTemplate.findOne(query2, Genre.class);
        assertThat(expectedGenre2).isNotNull();
        compareGenres(GENRE_2, expectedGenre2);
        List<Genre> actualGenres = genreDao.findByTitleIn(List.of(expectedGenre1.getTitle(), expectedGenre2.getTitle(), "TEST"));
        assertThat(actualGenres).isNotEmpty()
            .containsExactlyInAnyOrder(expectedGenre1, expectedGenre2);
    }

    private void compareGenres(Genre actualGenre, Genre expectedGenre) {
        assertThat(actualGenre)
            .usingRecursiveComparison()
            .isEqualTo(expectedGenre);
    }
}
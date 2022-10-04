package ru.otus.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.domain.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Repository
public class GenreDaoJdbc implements GenreDao {

    private static final GenreMapper GENRE_ROW_MAPPER = new GenreMapper();

    private final NamedParameterJdbcOperations jdbc;

    public GenreDaoJdbc(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.jdbc = namedParameterJdbcOperations;
    }

    @Override
    public long insert(Genre genre) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("insert into genre (title) values (:title)", genreToParams(genre), keyHolder, new String[] {"id"});
        return keyHolder.getKey().longValue();
    }

    private SqlParameterSource genreToParams(Genre genre) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", genre.getTitle());
        return params;
    }

    @Override
    public Optional<Genre> getById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "select id, title from genre where id = :id", params, GENRE_ROW_MAPPER
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Genre> getByTitle(String title) {
        Map<String, Object> params = Collections.singletonMap("title", title);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "select id, title from genre where title = :title", params, GENRE_ROW_MAPPER
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private static class GenreMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong("id");
            String title = resultSet.getString("title");
            return new Genre(id, title);
        }
    }

}

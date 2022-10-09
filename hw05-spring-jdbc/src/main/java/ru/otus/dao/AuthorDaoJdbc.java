package ru.otus.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.domain.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Repository
public class AuthorDaoJdbc implements AuthorDao {

    private static final AuthorMapper AUTHOR_ROW_MAPPER = new AuthorMapper();

    private final NamedParameterJdbcOperations jdbc;

    public AuthorDaoJdbc(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.jdbc = namedParameterJdbcOperations;
    }

    @Override
    public long insert(Author author) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("insert into author (name) values (:name)", authorToParams(author), keyHolder, new String[] {"id"});
        return keyHolder.getKey().longValue();
    }

    private SqlParameterSource authorToParams(Author author) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", author.getName());
        return params;
    }

    @Override
    public Optional<Author> getById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "select id, name from author where id = :id", params, AUTHOR_ROW_MAPPER
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Author> getByName(String name) {
        Map<String, Object> params = Collections.singletonMap("name", name);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
            "select id, name from author where name = :name", params, AUTHOR_ROW_MAPPER
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private static class AuthorMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            return new Author(id, name);
        }
    }

}

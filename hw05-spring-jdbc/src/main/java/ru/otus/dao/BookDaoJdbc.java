package ru.otus.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class BookDaoJdbc implements BookDao {

    private static final BookMapper BOOK_ROW_MAPPER = new BookMapper();

    private final NamedParameterJdbcOperations jdbc;

    public BookDaoJdbc(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.jdbc = namedParameterJdbcOperations;
    }

    @Override
    public long count() {
        Long count = jdbc.queryForObject("select count(*) from book", new MapSqlParameterSource(), Long.class);
        return count == null ? 0 : count;
    }

    @Override
    public long insert(Book book) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("insert into book (title, author_id, genre_id) values (:title, :authorId, :genreId)", bookToParams(book, false), keyHolder, new String[] {"id"});
        return keyHolder.getKey().longValue();
    }

    @Override
    public boolean update(Book book) {
        return jdbc.update("update book set title = :title, author_id = :authorId, genre_id = :genreId where id = :id", bookToParams(book, true)) > 0;
    }

    @Override
    public boolean deleteById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        return jdbc.update("delete from book where id = :id", params) > 0;
    }

    private SqlParameterSource bookToParams(Book book, boolean withId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("authorId", (book.getAuthor() == null) ? null : book.getAuthor().getId());
        params.addValue("genreId", (book.getGenre() == null) ? null : book.getGenre().getId());
        if (withId) {
            params.addValue("id", book.getId());
        }
        return params;
    }

    @Override
    public Optional<Book> getById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                "select b.id, b.title, a.id as author_id, a.name as author_name, g.id as genre_id, g.title as genre_title \n" +
                    "from book b \n" +
                    "  join author a on b.author_id = a.id \n" +
                    "  join genre g on b.genre_id = g.id \n" +
                    "where b.id = :id", params, BOOK_ROW_MAPPER
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> getAll() {
        return jdbc.query(
            "select b.id, b.title, a.id as author_id, a.name as author_name, g.id as genre_id, g.title as genre_title \n" +
                "from book b \n" +
                "  join author a on b.author_id = a.id \n" +
                "  join genre g on b.genre_id = g.id \n", BOOK_ROW_MAPPER
        );
    }

    private static class BookMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong("id");
            String title = resultSet.getString("title");

            long authorId = resultSet.getLong("author_id");
            String authorName = resultSet.getString("author_name");

            long genreId = resultSet.getLong("genre_id");
            String genreTitle = resultSet.getString("genre_title");

            return new Book(id, title, new Author(authorId, authorName), new Genre(genreId, genreTitle));
        }
    }

    private static class BookGenreMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet resultSet, int i) throws SQLException {
            long id = resultSet.getLong("id");
            String title = resultSet.getString("title");

            long authorId = resultSet.getLong("author_id");
            String authorName = resultSet.getString("author_name");

            long genreId = resultSet.getLong("genre_id");
            String genreTitle = resultSet.getString("genre_title");

            return new Book(id, title, new Author(authorId, authorName), new Genre(genreId, genreTitle));
        }
    }
}

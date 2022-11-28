package ru.otus.mongock.changelog;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.dao.AuthorDao;
import ru.otus.dao.BookDao;
import ru.otus.dao.CommentDao;
import ru.otus.dao.GenreDao;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.domain.Genre;

import java.util.ArrayList;
import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    private final List<Author> authors = new ArrayList<>();
    private final List<Genre> genres = new ArrayList<>();
    private final List<Comment> comments = new ArrayList<>();
    private final List<Book> books = new ArrayList<>();

    @ChangeSet(order = "001", id = "dropDb", author = "olga", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "initAuthors", author = "olga")
    public void initAuthors(AuthorDao authorDao) {
        authors.add(authorDao.save(new Author(null, "Катя Петрова")).block());
        authors.add(authorDao.save(new Author(null, "Женя Максимова")).block());
        authors.add(authorDao.save(new Author(null, "Иван Алексеев")).block());
    }

    @ChangeSet(order = "003", id = "initGenres", author = "olga")
    public void initGenres(GenreDao genreDao) {
        genres.add(genreDao.save(new Genre(null, "Детектив")).block());
        genres.add(genreDao.save(new Genre(null, "Фантастика")).block());
        genres.add(genreDao.save(new Genre(null, "Сказка")).block());
        genres.add(genreDao.save(new Genre(null, "Проза")).block());
        genres.add(genreDao.save(new Genre(null, "Поззия")).block());
    }

    @ChangeSet(order = "004", id = "initComments", author = "olga")
    public void initComments(CommentDao commentDao) {
        comments.add(commentDao.save(new Comment(null, "Интересная")).block());
        comments.add(commentDao.save(new Comment(null, "Увлекательная")).block());
        comments.add(commentDao.save(new Comment(null, "Скучная")).block());
        comments.add(commentDao.save(new Comment(null, "Захватывающая")).block());
        comments.add(commentDao.save(new Comment(null, "Необычная")).block());
        comments.add(commentDao.save(new Comment(null, "Смешная")).block());
        comments.add(commentDao.save(new Comment(null, "Интересная")).block());
        comments.add(commentDao.save(new Comment(null, "Захватывающая")).block());
    }

    @ChangeSet(order = "005", id = "initBooks", author = "olga")
    public void initBooks(BookDao bookDao) {
        books.add(bookDao.save(new Book(null, "Мой детектив", authors.get(0), List.of(genres.get(0), genres.get(3)), List.of(comments.get(0), comments.get(1)))).block());
        books.add(bookDao.save(new Book(null, "Необычная фантастика", authors.get(2), List.of(genres.get(1), genres.get(3)), List.of(comments.get(2)))).block());
        books.add(bookDao.save(new Book(null, "Странная сказка", authors.get(1), List.of(genres.get(2), genres.get(4)), List.of(comments.get(3), comments.get(4)))).block());
        books.add(bookDao.save(new Book(null, "Смешной детектив", authors.get(1), List.of(genres.get(0), genres.get(3)), List.of(comments.get(5)))).block());
        books.add(bookDao.save(new Book(null, "Страшная фантастика", authors.get(2), List.of(genres.get(1), genres.get(4)), List.of(comments.get(6), comments.get(7)))).block());
    }
}
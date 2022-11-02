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
        authors.add(authorDao.save(new Author("Катя Петрова")));
        authors.add(authorDao.save(new Author("Женя Максимова")));
        authors.add(authorDao.save(new Author("Иван Алексеев")));
    }

    @ChangeSet(order = "003", id = "initGenres", author = "olga")
    public void initGenres(GenreDao genreDao) {
        genres.add(genreDao.save(new Genre("Детектив")));
        genres.add(genreDao.save(new Genre("Фантастика")));
        genres.add(genreDao.save(new Genre("Сказка")));
        genres.add(genreDao.save(new Genre("Проза")));
        genres.add(genreDao.save(new Genre("Поззия")));
    }

    @ChangeSet(order = "004", id = "initComments", author = "olga")
    public void initComments(CommentDao commentDao) {
        comments.add(commentDao.save(new Comment("Интересная")));
        comments.add(commentDao.save(new Comment("Увлекательная")));
        comments.add(commentDao.save(new Comment("Скучная")));
        comments.add(commentDao.save(new Comment("Захватывающая")));
        comments.add(commentDao.save(new Comment("Необычная")));
        comments.add(commentDao.save(new Comment("Смешная")));
        comments.add(commentDao.save(new Comment("Интересная")));
        comments.add(commentDao.save(new Comment("Захватывающая")));
    }

    @ChangeSet(order = "005", id = "initBooks", author = "olga")
    public void initBooks(BookDao bookDao) {
        books.add(bookDao.save(new Book("Мой детектив", authors.get(0), List.of(genres.get(0), genres.get(3)), List.of(comments.get(0), comments.get(1)))));
        books.add(bookDao.save(new Book("Необычная фантастика", authors.get(2), List.of(genres.get(1), genres.get(3)), List.of(comments.get(2)))));
        books.add(bookDao.save(new Book("Странная сказка", authors.get(1), List.of(genres.get(2), genres.get(4)), List.of(comments.get(3), comments.get(4)))));
        books.add(bookDao.save(new Book("Смешной детектив", authors.get(1), List.of(genres.get(0), genres.get(3)), List.of(comments.get(5)))));
        books.add(bookDao.save(new Book("Страшная фантастика", authors.get(2), List.of(genres.get(1), genres.get(4)), List.of(comments.get(6), comments.get(7)))));
    }
}
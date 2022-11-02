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

import java.util.List;

@ChangeLog
public class MongoDatabaseChangelog {

    public static Author AUTHOR_1;
    public static Author AUTHOR_2;
    public static Genre GENRE_1;
    public static Genre GENRE_2;
    public static Comment COMMENT_1;
    public static Comment COMMENT_2;
    public static Comment COMMENT_3;
    public static Comment COMMENT_4;
    public static Comment COMMENT_5;
    public static Book BOOK_1;
    public static Book BOOK_2;
    public static Book BOOK_3;

    @ChangeSet(order = "001", id = "dropDb", author = "olga", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "initAuthors", author = "olga")
    public void initAuthors(AuthorDao authorDao) {
        AUTHOR_1 = authorDao.save(new Author("Катя Петрова"));
        AUTHOR_2 = authorDao.save(new Author("Женя Максимова"));
    }

    @ChangeSet(order = "003", id = "initGenres", author = "olga")
    public void initGenres(GenreDao genreDao) {
        GENRE_1 = genreDao.save(new Genre("Детектив"));
        GENRE_2 = genreDao.save(new Genre("Фантастика"));
    }

    @ChangeSet(order = "004", id = "initComments", author = "olga")
    public void initComments(CommentDao commentDao) {
        COMMENT_1 = commentDao.save(new Comment("Интересная"));
        COMMENT_2 = commentDao.save(new Comment("Увлекательная"));
        COMMENT_3 = commentDao.save(new Comment("Скучная"));
        COMMENT_4 = commentDao.save(new Comment("Захватывающая"));
        COMMENT_5 = commentDao.save(new Comment("Необычная"));
    }

    @ChangeSet(order = "005", id = "initBooks", author = "olga")
    public void initBooks(BookDao bookDao) {
        BOOK_1 = bookDao.save(new Book("Мой детектив", AUTHOR_1, List.of(GENRE_1), List.of(COMMENT_1, COMMENT_2)));
        BOOK_2 = bookDao.save(new Book("Необычная фантастика", AUTHOR_2, List.of(GENRE_2), List.of(COMMENT_3)));
        BOOK_3 = bookDao.save(new Book("Странная сказка", AUTHOR_2, List.of(GENRE_1, GENRE_2), List.of(COMMENT_4, COMMENT_5)));
    }
}
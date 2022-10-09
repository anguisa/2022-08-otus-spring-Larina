package ru.otus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dao.AuthorDao;
import ru.otus.dao.BookDao;
import ru.otus.dao.GenreDao;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Genre;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final GenreDao genreDao;

    public BookServiceImpl(BookDao bookDao, AuthorDao authorDao, GenreDao genreDao) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
        this.genreDao = genreDao;
    }

    @Transactional
    @Override
    public Book insert(Book book) {
        populateAuthorAndGenreId(book);

        long bookId = bookDao.insert(book);
        book.setId(bookId);

        return book;
    }

    @Transactional
    @Override
    public Optional<Book> update(Book book) {
        populateAuthorAndGenreId(book);

        boolean updated = bookDao.update(book);

        return updated ? Optional.of(book) : Optional.empty();
    }

    @Transactional
    @Override
    public boolean deleteById(long id) {
        return bookDao.deleteById(id);
    }

    @Override
    public Optional<Book> getById(long id) {
        return bookDao.getById(id);
    }

    @Override
    public List<Book> getAll() {
        return bookDao.getAll();
    }

    @Override
    public long count() {
        return bookDao.count();
    }

    private long getOrInsertBookAuthorId(Book book) {
        return authorDao.getByName(book.getAuthor().getName()).map(Author::getId)
            .orElseGet(() -> authorDao.insert(book.getAuthor()));
    }

    private long getOrInsertBookGenreId(Book book) {
        return genreDao.getByTitle(book.getGenre().getTitle()).map(Genre::getId)
            .orElseGet(() -> genreDao.insert(book.getGenre()));
    }

    private void populateAuthorAndGenreId(Book book) {
        populateAuthorId(book);
        populateGenreId(book);
    }

    private void populateAuthorId(Book book) {
        book.getAuthor().setId(getOrInsertBookAuthorId(book));
    }

    private void populateGenreId(Book book) {
        book.getGenre().setId(getOrInsertBookGenreId(book));
    }
}

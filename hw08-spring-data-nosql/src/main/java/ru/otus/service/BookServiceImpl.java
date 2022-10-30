package ru.otus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dao.BookDao;
import ru.otus.dao.CommentDao;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.domain.Genre;
import ru.otus.dto.BookDto;
import ru.otus.dto.converter.DtoConverter;
import ru.otus.exception.BookNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;
    private final CommentDao commentDao;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final DtoConverter<Book, BookDto> bookConverter;

    public BookServiceImpl(BookDao bookDao,
                           CommentDao commentDao,
                           AuthorService authorService,
                           GenreService genreService,
                           DtoConverter<Book, BookDto> bookConverter) {
        this.bookDao = bookDao;
        this.commentDao = commentDao;
        this.authorService = authorService;
        this.genreService = genreService;
        this.bookConverter = bookConverter;
    }

    @Transactional
    @Override
    public BookDto insert(BookDto bookDto) {
        Book book = bookConverter.fromDto(bookDto);
        populateAuthorAndGenres(book);
        return bookConverter.toDto(bookDao.save(book));
    }

    @Transactional
    @Override
    public BookDto update(BookDto bookDto) {
        Book book = bookConverter.fromDto(bookDto);
        populateAuthorAndGenres(book);
        return bookConverter.toDto(bookDao.save(book));
    }

    @Transactional
    @Override
    public void deleteById(String id) {
        Book book = bookDao.findById(id).orElseThrow((Supplier<RuntimeException>) () -> new BookNotFoundException(id));
        if (book.getComments() != null) {
            commentDao.deleteAllById(book.getComments().stream().map(Comment::getId).collect(Collectors.toList()));
        }
        bookDao.deleteById(id);
    }

    @Override
    public Optional<BookDto> findById(String id) {
        return bookDao.findById(id).map(bookConverter::toDto);
    }

    @Override
    public List<BookDto> findAll() {
        return bookDao.findAll().stream().map(bookConverter::toDto).collect(Collectors.toList());
    }

    @Override
    public long count() {
        return bookDao.count();
    }

    private void populateAuthorAndGenres(Book book) {
        populateAuthor(book);
        populateGenres(book);
    }

    private void populateAuthor(Book book) {
        book.setAuthor(authorService.findByNameOrCreate(book.getAuthor().getName()));
    }

    private void populateGenres(Book book) {
        book.setGenres(genreService.findByTitleInOrCreate(book.getGenres().stream().map(Genre::getTitle).collect(Collectors.toList())));
    }

}

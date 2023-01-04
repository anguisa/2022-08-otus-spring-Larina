package ru.otus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dao.BookDao;
import ru.otus.domain.Book;
import ru.otus.dto.BookDto;
import ru.otus.dto.converter.DtoConverter;
import ru.otus.exception.BookNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;
    private final DtoConverter<Book, BookDto> bookConverter;

    public BookServiceImpl(BookDao bookDao, DtoConverter<Book, BookDto> bookConverter) {
        this.bookDao = bookDao;
        this.bookConverter = bookConverter;
    }

    @Transactional
    @Override
    public BookDto insert(BookDto bookDto) {
        Book book = bookConverter.fromDto(bookDto);
        return bookConverter.toDto(bookDao.save(book));
    }

    @Transactional
    @Override
    public BookDto update(BookDto bookDto) {
        Book book = bookConverter.fromDto(bookDto);
        if (book.getId() == null || bookDao.findById(book.getId()).isEmpty()) {
            throw new BookNotFoundException((book.getId() == null) ? 0L : book.getId());
        }
        return bookConverter.toDto(bookDao.save(book));
    }

    @Override
    public void deleteById(long id) {
        bookDao.deleteById(id);
    }

    @Override
    public Optional<BookDto> findById(long id) {
        return bookDao.findById(id).map(bookConverter::toDto);
    }

    @Override
    public List<BookDto> findAll() {
        return bookDao.findAll().stream().map(bookConverter::toDto).collect(Collectors.toList());
    }

}

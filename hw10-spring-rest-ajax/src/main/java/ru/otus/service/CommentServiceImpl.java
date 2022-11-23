package ru.otus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dao.BookDao;
import ru.otus.dao.CommentDao;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.dto.CommentDto;
import ru.otus.dto.converter.DtoConverter;
import ru.otus.exception.BookNotFoundException;
import ru.otus.exception.CommentNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final BookDao bookDao;
    private final DtoConverter<Comment, CommentDto> commentConverter;

    public CommentServiceImpl(CommentDao commentDao, BookDao bookDao, DtoConverter<Comment, CommentDto> commentConverter) {
        this.commentDao = commentDao;
        this.bookDao = bookDao;
        this.commentConverter = commentConverter;
    }

    @Transactional
    @Override
    public CommentDto insert(CommentDto commentDto) {
        Comment comment = commentConverter.fromDto(commentDto);
        populateBook(comment);
        return commentConverter.toDto(commentDao.save(comment));
    }

    @Transactional
    @Override
    public CommentDto update(CommentDto commentDto) {
        Comment comment = commentConverter.fromDto(commentDto);
        if (comment.getId() == null || commentDao.findById(comment.getId()).isEmpty()) {
            throw new CommentNotFoundException((comment.getId() == null) ? 0L : comment.getId());
        }
        populateBook(comment);
        return commentConverter.toDto(commentDao.save(comment));
    }

    @Override
    public void deleteById(long id) {
        commentDao.deleteById(id);
    }

    @Override
    public Optional<CommentDto> findById(long id) {
        return commentDao.findById(id).map(commentConverter::toDto);
    }

    @Transactional(readOnly = true) // чтобы могли извлечься LAZY-сущности (комментарий из книги), которые не инициализируются в dao
    @Override
    public List<CommentDto> findByBookId(long bookId) {
        Book book = getBook(bookId);
        if (book.getComments() == null) {
            return List.of();
        }
        return getBook(bookId).getComments().stream().map(commentConverter::toDto).collect(Collectors.toList());
    }

    private void populateBook(Comment comment) {
        comment.setBook(getCommentBook(comment));
    }

    private Book getCommentBook(Comment comment) {
        return getBook(comment.getBook().getId());
    }

    private Book getBook(long bookId) {
        Optional<Book> book = bookDao.findById(bookId);
        if (book.isEmpty()) {
            throw new BookNotFoundException(bookId);
        }
        return book.get();
    }

}

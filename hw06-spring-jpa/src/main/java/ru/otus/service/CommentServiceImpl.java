package ru.otus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dao.BookDao;
import ru.otus.dao.CommentDao;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.dto.CommentDto;
import ru.otus.exception.BookNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final BookDao bookDao;

    public CommentServiceImpl(CommentDao commentDao, BookDao bookDao) {
        this.commentDao = commentDao;
        this.bookDao = bookDao;
    }

    @Transactional
    @Override
    public CommentDto insert(CommentDto commentDto) {
        Comment comment = commentDto.fromDto();
        populateBook(comment);
        return new CommentDto(commentDao.save(comment));
    }

    @Transactional
    @Override
    public CommentDto update(CommentDto commentDto) {
        Comment comment = commentDto.fromDto();
        populateBook(comment);
        return new CommentDto(commentDao.save(comment));
    }

    @Transactional
    @Override
    public boolean deleteById(long id) {
        return commentDao.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<CommentDto> getById(long id) {
        return commentDao.getById(id).map(CommentDto::new);
    }

    @Transactional(readOnly = true) // чтобы могли извлечься LAZY-сущности, которые не инициализируются в dao
    @Override
    public List<CommentDto> getByBookId(long bookId) {
        return commentDao.getByBookId(bookId).stream().map(CommentDto::new).collect(Collectors.toList());
    }

    private void populateBook(Comment comment) {
        comment.setBook(getCommentBook(comment));
    }

    private Book getCommentBook(Comment comment) {
        Optional<Book> book = bookDao.getById(comment.getBook().getId());
        if (book.isEmpty()) {
            throw new BookNotFoundException(comment.getBook().getId());
        }
        return book.get();
    }

}

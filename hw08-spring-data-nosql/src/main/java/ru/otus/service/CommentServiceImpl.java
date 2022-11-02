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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final BookDao bookDao;
    private final DtoConverter<Comment, CommentDto> commentConverter;

    public CommentServiceImpl(CommentDao commentDao,
                              BookDao bookDao,
                              DtoConverter<Comment, CommentDto> commentConverter) {
        this.commentDao = commentDao;
        this.bookDao = bookDao;
        this.commentConverter = commentConverter;
    }

    @Transactional
    @Override
    public CommentDto insert(CommentDto commentDto, String bookId) {
        Book book = getBook(bookId);
        Comment comment = commentConverter.fromDto(commentDto);
        comment = commentDao.save(comment);
        if (book.getComments() == null) {
            book.setComments(new ArrayList<>());
        }
        book.getComments().add(comment);
        bookDao.save(book);
        return commentConverter.toDto(comment);
    }

    @Transactional
    @Override
    public CommentDto update(CommentDto commentDto) {
        Comment comment = commentConverter.fromDto(commentDto);
        return commentConverter.toDto(commentDao.save(comment));
    }

    @Transactional
    @Override
    public void deleteByIdAndBookId(String id, String bookId) {
        commentDao.deleteById(id);
        bookDao.deleteCommentByIdAndBookId(id, bookId);
    }

    @Override
    public Optional<CommentDto> findById(String id) {
        return commentDao.findById(id).map(commentConverter::toDto);
    }

    @Override
    public List<CommentDto> findByBookId(String bookId) {
        Book book = getBook(bookId);
        if (book.getComments() == null) {
            return List.of();
        }
        return getBook(bookId).getComments().stream().map(commentConverter::toDto).collect(Collectors.toList());
    }

    private Book getBook(String bookId) {
        return bookDao.findById(bookId).orElseThrow((Supplier<RuntimeException>) () -> new BookNotFoundException(bookId));
    }

}

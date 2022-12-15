package ru.otus.service;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dao.BookDao;
import ru.otus.dao.CommentDao;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.dto.CommentDto;
import ru.otus.dto.converter.DtoConverter;
import ru.otus.exception.BookNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final BookDao bookDao;
    private final DtoConverter<Comment, CommentDto> commentConverter;
    private final MutableAclService mutableAclService;

    public CommentServiceImpl(CommentDao commentDao, BookDao bookDao, DtoConverter<Comment, CommentDto> commentConverter, MutableAclService mutableAclService) {
        this.commentDao = commentDao;
        this.bookDao = bookDao;
        this.commentConverter = commentConverter;
        this.mutableAclService = mutableAclService;
    }

    @Transactional
    @Override
    public CommentDto insert(CommentDto commentDto) {
        Comment comment = commentConverter.fromDto(commentDto);
        populateBook(comment);
        CommentDto insertedComment = commentConverter.toDto(commentDao.save(comment));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Sid owner = new PrincipalSid(authentication);
        ObjectIdentity oid = new ObjectIdentityImpl(insertedComment.getClass(), insertedComment.getId());

        String adminPrincipal = "admin";
        Sid admin = new PrincipalSid(adminPrincipal);
        MutableAcl acl = mutableAclService.createAcl(oid);
        acl.setOwner(owner);
        CumulativePermission cumulativePermissionForAdmin = new CumulativePermission();
        cumulativePermissionForAdmin.set(BasePermission.ADMINISTRATION);
        cumulativePermissionForAdmin.set(BasePermission.CREATE);
        cumulativePermissionForAdmin.set(BasePermission.DELETE);
        cumulativePermissionForAdmin.set(BasePermission.READ);
        cumulativePermissionForAdmin.set(BasePermission.WRITE);
        acl.insertAce(acl.getEntries().size(), cumulativePermissionForAdmin, admin, true);

        if (!adminPrincipal.equals(authentication.getName())) {
            CumulativePermission cumulativePermissionForOwner = new CumulativePermission();
            cumulativePermissionForOwner.set(BasePermission.CREATE);
            cumulativePermissionForOwner.set(BasePermission.DELETE);
            cumulativePermissionForOwner.set(BasePermission.READ);
            cumulativePermissionForOwner.set(BasePermission.WRITE);
            acl.insertAce(acl.getEntries().size(), cumulativePermissionForOwner, owner, true);
        }
        mutableAclService.updateAcl(acl);

        return insertedComment;
    }

    @PreAuthorize("hasPermission(#commentDto.id, 'ru.otus.dto.CommentDto', 'WRITE')")
    @Transactional
    @Override
    public CommentDto update(CommentDto commentDto) {
        Comment comment = commentConverter.fromDto(commentDto);
        populateBook(comment);
        return commentConverter.toDto(commentDao.save(comment));
    }

    @PreAuthorize("hasPermission(#id, 'ru.otus.dto.CommentDto', 'DELETE')")
    @Override
    public void deleteById(long id) {
        commentDao.deleteById(id);
    }

    @PreAuthorize("hasPermission(#id, 'ru.otus.dto.CommentDto', 'READ')")
    @Override
    public Optional<CommentDto> findById(long id) {
        return commentDao.findById(id).map(commentConverter::toDto);
    }

    @PostFilter("hasPermission(filterObject, 'READ')")
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

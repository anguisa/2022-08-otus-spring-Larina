package ru.otus.service;

import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.dao.BookDao;
import ru.otus.dao.CommentDao;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.domain.Genre;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.BookShortDto;
import ru.otus.dto.CommentDto;
import ru.otus.dto.GenreDto;
import ru.otus.exception.BookNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Тест сервиса комментариев к книгам")
@SpringBootTest
public class CommentServiceImplTest {

    private static final Author EXPECTED_AUTHOR = new Author(1L, "Маша Васильева");
    private static final AuthorDto EXPECTED_AUTHOR_DTO = new AuthorDto(EXPECTED_AUTHOR.getId(), EXPECTED_AUTHOR.getName());
    private static final Genre EXPECTED_GENRE = new Genre(1L, "Стихотворение");
    private static final GenreDto EXPECTED_GENRE_DTO = new GenreDto(EXPECTED_GENRE.getId(), EXPECTED_GENRE.getTitle());
    private static final Book EXPECTED_BOOK = new Book(1L, "Короткое стихотворение", EXPECTED_AUTHOR, List.of(EXPECTED_GENRE), null);
    private static final BookShortDto EXPECTED_BOOK_DTO = new BookShortDto(EXPECTED_BOOK.getId());
    private static final Comment EXPECTED_COMMENT = new Comment(1L, "Интересно", EXPECTED_BOOK);
    private static final CommentDto EXPECTED_COMMENT_DTO = new CommentDto(EXPECTED_COMMENT.getId(), EXPECTED_COMMENT.getText(), EXPECTED_BOOK_DTO);
    private static final Comment EXPECTED_COMMENT_2 = new Comment(2L, "Скучно", EXPECTED_BOOK);
    private static final CommentDto EXPECTED_COMMENT_DTO_2 = new CommentDto(EXPECTED_COMMENT_2.getId(), EXPECTED_COMMENT_2.getText(), EXPECTED_BOOK_DTO);
    private static final Book EXPECTED_BOOK_WITH_COMMENTS = new Book(1L, "Короткое стихотворение", EXPECTED_AUTHOR, List.of(EXPECTED_GENRE), List.of(EXPECTED_COMMENT, EXPECTED_COMMENT_2));

    @Autowired
    private CommentService commentService;

    @MockBean
    private BookDao bookDao;

    @MockBean
    private CommentDao commentDao;

    @MockBean
    private MutableAclService mutableAclService;

    // чтобы не поднималась база; используем только сервис и конвертеры
    @Configuration
    @Import(CommentServiceImpl.class)
    @ComponentScan("ru.otus.dto.converter")
    static class CommentServiceImplConfiguration {
    }

    @BeforeEach
    public void setUp() {
        when(bookDao.findById(EXPECTED_BOOK.getId())).thenReturn(Optional.of(EXPECTED_BOOK_WITH_COMMENTS));

        when(commentDao.save(any())).thenReturn(EXPECTED_COMMENT);
        when(commentDao.findById(EXPECTED_COMMENT.getId())).thenReturn(Optional.of(EXPECTED_COMMENT));
    }

    @DisplayName("Добавляет комментарий к книге в БД и создаёт один ACL для админа")
    @WithMockUser(username = "admin")
    @Test
    void shouldInsertCommentAndCreateAclForAdmin() {
        MutableAcl acl = mock(MutableAcl.class);
        when(mutableAclService.createAcl(any())).thenReturn(acl);
        when(acl.getEntries()).thenReturn(List.of());

        CommentDto expectedComment = EXPECTED_COMMENT_DTO;

        expectedComment = commentService.insert(expectedComment);

        Optional<CommentDto> actualComment = commentService.findById(expectedComment.getId());
        compareComments(actualComment, expectedComment);

        verify(mutableAclService, times(1)).updateAcl(any());
        verify(acl, times(1)).insertAce(
            eq(0),
            argThat(permission -> permission.getMask() == 31 && permission instanceof CumulativePermission),
            argThat(sid -> sid instanceof PrincipalSid && ((PrincipalSid) sid).getPrincipal().equals("admin")),
            eq(true)
        );
    }

    @DisplayName("Добавляет комментарий к книге в БД и создаёт два ACL - для админа и пользователя")
    @WithMockUser(username = "user")
    @Test
    void shouldInsertCommentAndCreateAclForAdminAndUser() {
        MutableAcl acl = mock(MutableAcl.class);
        when(mutableAclService.createAcl(any())).thenReturn(acl);
        when(acl.getEntries()).thenReturn(List.of()).thenReturn(List.of(mock(AccessControlEntry.class)));

        CommentDto expectedComment = EXPECTED_COMMENT_DTO;

        expectedComment = commentService.insert(expectedComment);

        Optional<CommentDto> actualComment = commentService.findById(expectedComment.getId());
        compareComments(actualComment, expectedComment);

        ArgumentCaptor<Integer> indexCapture = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Permission> permissionCapture = ArgumentCaptor.forClass(Permission.class);
        ArgumentCaptor<Sid> sidCapture = ArgumentCaptor.forClass(Sid.class);
        ArgumentCaptor<Boolean> grantingCapture = ArgumentCaptor.forClass(Boolean.class);

        verify(mutableAclService, times(1)).updateAcl(any());
        verify(acl, times(2)).insertAce(indexCapture.capture(), permissionCapture.capture(), sidCapture.capture(), grantingCapture.capture());

        List<Integer> indexes = indexCapture.getAllValues();
        List<Permission> permissions = permissionCapture.getAllValues();
        List<Sid> sids = sidCapture.getAllValues();
        List<Boolean> grantings = grantingCapture.getAllValues();

        assertThat(indexes).size().isEqualTo(2);
        assertThat(permissions).size().isEqualTo(2);
        assertThat(sids).size().isEqualTo(2);
        assertThat(grantings).size().isEqualTo(2);

        assertThat(indexes.get(0)).isEqualTo(0);
        assertThat(indexes.get(1)).isEqualTo(1);

        assertThat(permissions.get(0)).isInstanceOf(CumulativePermission.class).matches(p -> p.getMask() == 31);
        assertThat(permissions.get(1)).isInstanceOf(CumulativePermission.class).matches(p -> p.getMask() == 15);

        assertThat(sids.get(0)).isInstanceOf(PrincipalSid.class).matches(p -> ((PrincipalSid) p).getPrincipal().equals("admin"));
        assertThat(sids.get(1)).isInstanceOf(PrincipalSid.class).matches(p -> ((PrincipalSid) p).getPrincipal().equals("user"));

        assertThat(grantings.get(0)).isEqualTo(true);
        assertThat(grantings.get(1)).isEqualTo(true);
    }

    @DisplayName("Бросает исключение при добавлении комментария к несуществующей книге")
    @Test
    void shouldFailWhenInsertCommentWithNotExistedBook() {
        Comment comment = new Comment(1L, "Интересно", new Book(2L, "Книга", EXPECTED_AUTHOR, List.of(EXPECTED_GENRE), null));
        CommentDto commentDto = new CommentDto(comment.getId(), comment.getText(), new BookShortDto(comment.getBook().getId()));

        assertThatThrownBy(() -> commentService.insert(commentDto))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessageContaining("Book 2 not found");
    }

    @DisplayName("Обновляет комментарий к книге в БД")
    @Test
    void shouldUpdateComment() {
        CommentDto initialComment = EXPECTED_COMMENT_DTO;

        Optional<CommentDto> commentBeforeUpdate = commentService.findById(initialComment.getId());
        compareComments(commentBeforeUpdate, initialComment);

        String randomTxt = UUID.randomUUID().toString();

        Book bookNew = new Book(2L, "My book " + randomTxt, EXPECTED_AUTHOR, List.of(EXPECTED_GENRE), null);
        when(bookDao.findById(bookNew.getId())).thenReturn(Optional.of(bookNew));

        Comment updatedComment = new Comment(initialComment.getId(), "My title " + randomTxt, bookNew);
        CommentDto updatedCommentDto = new CommentDto(updatedComment.getId(), updatedComment.getText(), new BookShortDto(bookNew.getId()));
        when(commentDao.findById(initialComment.getId())).thenReturn(Optional.of(updatedComment));

        commentService.update(updatedCommentDto);

        Optional<CommentDto> actualComment = commentService.findById(initialComment.getId());
        compareComments(actualComment, updatedCommentDto);
    }

    @DisplayName("Удаляет комментарий к книге из БД")
    @Test
    void shouldDeleteComment() {
        CommentDto expectedComment = EXPECTED_COMMENT_DTO;

        when(commentDao.findById(expectedComment.getId())).thenReturn(Optional.empty());

        commentService.deleteById(expectedComment.getId());

        Optional<CommentDto> actualComment = commentService.findById(expectedComment.getId());
        assertThat(actualComment).isEmpty();
    }

    @DisplayName("Возвращает ожидаемый комментарий к книге по id")
    @Test
    void shouldReturnExpectedCommentById() {
        CommentDto expectedComment = EXPECTED_COMMENT_DTO;
        Optional<CommentDto> actualComment = commentService.findById(expectedComment.getId());
        compareComments(actualComment, expectedComment);
    }

    @DisplayName("Возвращает ожидаемый список комментариев по id книги")
    @Test
    void shouldReturnExpectedCommentsByBookId() {
        List<CommentDto> expectedComments = List.of(EXPECTED_COMMENT_DTO, EXPECTED_COMMENT_DTO_2);
        List<CommentDto> actualComments = commentService.findByBookId(EXPECTED_BOOK.getId());
        assertThat(actualComments)
            .usingRecursiveFieldByFieldElementComparator(RecursiveComparisonConfiguration.builder().build())
            .containsExactlyInAnyOrderElementsOf(expectedComments);
    }

    private void compareComments(Optional<CommentDto> actualComment, CommentDto expectedComment) {
        assertThat(actualComment)
            .isNotEmpty()
            .get()
            .usingRecursiveComparison()
            .isEqualTo(expectedComment);
    }
}

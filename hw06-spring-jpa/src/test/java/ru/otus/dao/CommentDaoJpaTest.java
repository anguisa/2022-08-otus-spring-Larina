package ru.otus.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.domain.Author;
import ru.otus.domain.Book;
import ru.otus.domain.Comment;
import ru.otus.domain.Genre;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тест DAO для комментариев к книгам")
@DataJpaTest
@Import(CommentDaoJpa.class)
class CommentDaoJpaTest {

    private static final Author EXPECTED_AUTHOR = new Author(1L, "Маша Васильева");
    private static final Genre EXPECTED_GENRE = new Genre(1L, "Стихотворение");
    private static final List<Book> EXPECTED_BOOKS = List.of(
        new Book(1L, "Мой детектив", EXPECTED_AUTHOR, List.of(EXPECTED_GENRE), null),
        new Book(2L, "Необычная фантастика", EXPECTED_AUTHOR, List.of(EXPECTED_GENRE), null),
        new Book(3L, "Смешной детектив", EXPECTED_AUTHOR, List.of(EXPECTED_GENRE), null)
    );
    private static final List<Comment> EXPECTED_COMMENTS = List.of(
        new Comment(1L, "Интересная", EXPECTED_BOOKS.get(0)),
        new Comment(2L, "Увлекательная", EXPECTED_BOOKS.get(0)),
        new Comment(3L, "Скучная", EXPECTED_BOOKS.get(1)),
        new Comment(4L, "Захватывающая", EXPECTED_BOOKS.get(2)),
        new Comment(5L, "Необычная", EXPECTED_BOOKS.get(2))
    );

    @Autowired
    private CommentDao commentDao;

    @Autowired
    private TestEntityManager em;

    @DisplayName("Добавляет комментарий к книге в БД")
    @Test
    void shouldInsertComment() {
        Book book = em.find(Book.class, EXPECTED_BOOKS.get(0).getId());

        String randomTxt = UUID.randomUUID().toString();
        Comment expectedComment = new Comment(null, "My text " + randomTxt, book);

        expectedComment = commentDao.save(expectedComment);

        Optional<Comment> actualComment = commentDao.getById(expectedComment.getId());

        assertThat(actualComment).isNotEmpty();
        compareComments(actualComment.get(), expectedComment);
    }

    @DisplayName("Обновляет комментарий к книге в БД")
    @Test
    void shouldUpdateComment() {
        Comment initialComment = em.find(Comment.class, EXPECTED_COMMENTS.get(0).getId());

        Optional<Comment> commentBeforeUpdate = commentDao.getById(initialComment.getId());
        assertThat(commentBeforeUpdate).isNotEmpty();
        compareComments(commentBeforeUpdate.get(), initialComment);

        Book book = em.find(Book.class, EXPECTED_BOOKS.get(2).getId());
        String randomTxt = UUID.randomUUID().toString();
        Comment updatedComment = new Comment(initialComment.getId(), "My title " + randomTxt, book);

        commentDao.save(updatedComment);

        Optional<Comment> actualComment = commentDao.getById(updatedComment.getId());

        assertThat(actualComment).isNotEmpty();
        compareComments(actualComment.get(), updatedComment);
    }

    @DisplayName("Удаляет комментарий к книге из БД")
    @Test
    void shouldDeleteComment() {
        Comment expectedComment = em.find(Comment.class, EXPECTED_COMMENTS.get(0).getId());

        boolean deleted = commentDao.deleteById(expectedComment.getId());
        assertThat(deleted).isEqualTo(true);

        Optional<Comment> actualComment = commentDao.getById(expectedComment.getId());
        assertThat(actualComment).isEmpty();
    }

    @DisplayName("Возвращает ожидаемый комментарий к книге по id")
    @Test
    void shouldReturnExpectedCommentById() {
        long expectedCommentId = EXPECTED_COMMENTS.get(0).getId();

        Comment expectedComment = em.find(Comment.class, expectedCommentId);
        Optional<Comment> actualComment = commentDao.getById(expectedCommentId);

        assertThat(actualComment).isNotEmpty();
        compareComments(actualComment.get(), expectedComment);
    }

    @DisplayName("Возвращает ожидаемый список комментариев по id книги")
    @Test
    void shouldReturnExpectedCommentsByBookId() {
        List<Comment> actualComments = commentDao.getByBookId(EXPECTED_BOOKS.get(0).getId());
        assertThat(actualComments).containsExactlyInAnyOrderElementsOf(List.of(EXPECTED_COMMENTS.get(0), EXPECTED_COMMENTS.get(1)));
    }

    private void compareComments(Comment actualComment, Comment expectedComment) {
        assertThat(actualComment)
            .usingRecursiveComparison()
            .isEqualTo(expectedComment);
    }
}
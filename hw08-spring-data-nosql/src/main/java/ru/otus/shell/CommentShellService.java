package ru.otus.shell;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.dto.CommentDto;
import ru.otus.service.CommentService;

import java.util.List;
import java.util.Optional;

@ShellComponent
public class CommentShellService {

    private final CommentService commentService;

    public CommentShellService(CommentService commentService) {
        this.commentService = commentService;
    }

    // Пример использования: ic --t 'Новинка' --ib 1
    @ShellMethod(value = "Insert new comment", key = {"insert-comment", "ic"})
    public String insertComment(@ShellOption(help = "Comment text", value = {"--text", "--t"}) String commentText,
                                @ShellOption(help = "Book id", value = {"--id-book", "--ib"}) String bookId) {
        CommentDto comment = new CommentDto(commentText);
        return commentService.insert(comment, bookId).toString();
    }

    // Пример использования: uc --i 9 --t 'Бестселлер' --ib 1
    @ShellMethod(value = "Update comment", key = {"update-comment", "uc"})
    public String updateComment(@ShellOption(help = "Comment id", value = {"--id", "--i"}) String commentId,
                                @ShellOption(help = "Comment text", value = {"--text", "--t"}) String commentText) {
        CommentDto comment = new CommentDto(commentId, commentText);
        return commentService.update(comment).toString();
    }

    @ShellMethod(value = "Get comment by id", key = {"get-comment", "gc"})
    public String getCommentById(@ShellOption(help = "Comment id", value = {"--id", "--i"}) String commentId) {
        Optional<CommentDto> book = commentService.findById(commentId);
        return book.map(CommentDto::toString).orElse(null);
    }

    @ShellMethod(value = "Get all comments for book", key = {"get-book-comments", "gcb"})
    public List<CommentDto> getCommentByBookId(@ShellOption(help = "Book id", value = {"--id-book", "--ib"}) String bookId) {
        return commentService.findByBookId(bookId);
    }

    @ShellMethod(value = "Delete comment by id", key = {"delete-comment", "dc"})
    public void deleteCommentById(@ShellOption(help = "Comment id", value = {"--id", "--i"}) String commentId,
                                  @ShellOption(help = "Book id", value = {"--id-book", "--ib"}) String bookId) {
        commentService.deleteByIdAndBookId(commentId, bookId);
    }

}

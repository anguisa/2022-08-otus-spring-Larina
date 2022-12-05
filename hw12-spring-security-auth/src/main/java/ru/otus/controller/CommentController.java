package ru.otus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.dto.BookShortDto;
import ru.otus.dto.CommentDto;
import ru.otus.exception.CommentNotFoundException;
import ru.otus.service.CommentService;

import java.util.List;

@Controller
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/books/{bookId}/comments")
    public String listComments(@PathVariable("bookId") long bookId, Model model) {
        List<CommentDto> comments = commentService.findByBookId(bookId);
        model.addAttribute("comments", comments);
        model.addAttribute("bookId", bookId);
        return "list_comments";
    }

    @PostMapping("/books/{bookId}/comments/delete")
    public String deleteComment(@PathVariable("bookId") long bookId, @RequestParam("id") long id, Model model) {
        commentService.deleteById(id);
        return String.format("redirect:/books/%s/comments", bookId);
    }

    @GetMapping("/books/{bookId}/comments/edit")
    public String editComment(@PathVariable("bookId") long bookId, @RequestParam("id") long id, Model model) {
        CommentDto comment = commentService.findById(id).orElseThrow(() -> new CommentNotFoundException(id));
        model.addAttribute("comment", comment);
        return "edit_comment";
    }

    @PostMapping("/books/{bookId}/comments/edit")
    public String editComment(@PathVariable("bookId") long bookId, @ModelAttribute("comment") CommentDto comment, Model model) {
        commentService.update(comment);
        return String.format("redirect:/books/%s/comments", bookId);
    }

    @GetMapping("/books/{bookId}/comments/create")
    public String createComment(@PathVariable("bookId") long bookId, Model model) {
        model.addAttribute("comment", new CommentDto(null, "", new BookShortDto(bookId)));
        return "edit_comment";
    }

    @PostMapping("/books/{bookId}/comments/create")
    public String createComment(@PathVariable("bookId") long bookId, @ModelAttribute("comment") CommentDto comment, Model model) {
        commentService.insert(comment);
        return String.format("redirect:/books/%s/comments", bookId);
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<String> handleNotFound(CommentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

}

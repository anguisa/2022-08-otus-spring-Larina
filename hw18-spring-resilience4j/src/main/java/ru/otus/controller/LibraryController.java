package ru.otus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.service.AuthorService;
import ru.otus.service.GenreService;

@Controller
public class LibraryController {

    private final AuthorService authorService;
    private final GenreService genreService;

    public LibraryController(AuthorService authorService,
                             GenreService genreService) {
        this.authorService = authorService;
        this.genreService = genreService;
    }

    @GetMapping(value = "/")
    public String mainPage(Model model) {
        model.addAttribute("authors", authorService.listAuthors().collectList());
        model.addAttribute("genres", genreService.listGenres()
            .collectList());
        return "library";
    }
}

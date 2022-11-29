package ru.otus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.dao.AuthorDao;
import ru.otus.dao.GenreDao;
import ru.otus.domain.Author;
import ru.otus.domain.Genre;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.GenreDto;
import ru.otus.dto.converter.DtoConverter;

@Controller
public class LibraryController {

    private final AuthorDao authorDao;
    private final GenreDao genreDao;
    private final DtoConverter<Author, AuthorDto> authorConverter;
    private final DtoConverter<Genre, GenreDto> genreConverter;

    public LibraryController(AuthorDao authorDao,
                             GenreDao genreDao,
                             DtoConverter<Author, AuthorDto> authorConverter,
                             DtoConverter<Genre, GenreDto> genreConverter) {
        this.authorDao = authorDao;
        this.genreDao = genreDao;
        this.authorConverter = authorConverter;
        this.genreConverter = genreConverter;
    }

    @GetMapping(value = "/")
    public String mainPage(Model model) {
        model.addAttribute("authors", authorDao.findAll().map(authorConverter::toDto).collectList());
        model.addAttribute("genres", genreDao.findAll().map(genreConverter::toDto).collectList());
        return "library";
    }
}

package ru.otus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.dto.GenreDto;
import ru.otus.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/api/genres")
    public ResponseEntity<List<GenreDto>> listBooks() {
        return ResponseEntity.ok().body(genreService.findAll());
    }

}

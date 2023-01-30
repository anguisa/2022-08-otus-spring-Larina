package ru.otus.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.otus.dao.GenreDao;
import ru.otus.domain.Genre;
import ru.otus.dto.GenreDto;
import ru.otus.dto.converter.DtoConverter;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreDao genreDao;
    private final DtoConverter<Genre, GenreDto> genreConverter;

    public GenreServiceImpl(GenreDao genreDao,
                            DtoConverter<Genre, GenreDto> genreConverter) {
        this.genreDao = genreDao;
        this.genreConverter = genreConverter;
    }

    @Override
    @CircuitBreaker(name = "mongoDb", fallbackMethod = "listGenresFallback")
    @TimeLimiter(name = "mongoDb")
    @Retry(name = "mongoDb")
    public Flux<GenreDto> listGenres() {
        return genreDao.findAll()
            .map(genreConverter::toDto);
    }

    private Flux<GenreDto> listGenresFallback(Throwable t) {
        return Flux.just(new GenreDto("000", "Без названия"));
    }
}

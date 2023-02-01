package ru.otus.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import ru.otus.dao.AuthorDao;
import ru.otus.domain.Author;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.converter.DtoConverter;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorDao authorDao;
    private final DtoConverter<Author, AuthorDto> authorConverter;

    public AuthorServiceImpl(AuthorDao authorDao,
                             DtoConverter<Author, AuthorDto> authorConverter) {
        this.authorDao = authorDao;
        this.authorConverter = authorConverter;
    }

    @Override
    @CircuitBreaker(name = "mongoDb", fallbackMethod = "listAuthorsFallback")
    @TimeLimiter(name = "mongoDb")
    @Retry(name = "mongoDb")
    public Flux<AuthorDto> listAuthors() {
        return authorDao.findAll()
            .map(authorConverter::toDto);
    }

    private Flux<AuthorDto> listAuthorsFallback(Throwable t) {
        return Flux.just(new AuthorDto("00", "Без имени"));
    }
}

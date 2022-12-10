package ru.otus.service;

import org.springframework.stereotype.Service;
import ru.otus.dao.AuthorDao;
import ru.otus.domain.Author;
import ru.otus.dto.AuthorDto;
import ru.otus.dto.converter.DtoConverter;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorDao authorDao;
    private final DtoConverter<Author, AuthorDto> authorConverter;

    public AuthorServiceImpl(AuthorDao authorDao, DtoConverter<Author, AuthorDto> authorConverter) {
        this.authorDao = authorDao;
        this.authorConverter = authorConverter;
    }

    @Override
    public List<AuthorDto> findAll() {
        return authorDao.findAll().stream().map(authorConverter::toDto).collect(Collectors.toList());
    }
}

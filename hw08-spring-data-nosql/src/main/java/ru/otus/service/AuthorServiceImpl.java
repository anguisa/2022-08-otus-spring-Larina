package ru.otus.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dao.AuthorDao;
import ru.otus.domain.Author;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorDao authorDao;

    public AuthorServiceImpl(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    @Transactional
    @Override
    public Author findByNameOrCreate(String name) {
        Author author = authorDao.findByName(name).orElse(null);
        if (author == null) {
            author = authorDao.save(new Author(name));
        }
        return author;
    }

}

package ru.otus.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import ru.otus.domain.Book;

public interface BookDao extends ReactiveMongoRepository<Book, String>, BookDaoCustom {
}

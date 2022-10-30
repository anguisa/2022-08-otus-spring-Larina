package ru.otus.dao;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import ru.otus.domain.Book;

public class BookDaoCustomImpl implements BookDaoCustom {

    private final MongoTemplate mongoTemplate;

    public BookDaoCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void deleteCommentByIdAndBookId(String commentId, String bookId) {
        Query query = new Query(Criteria.where("id").is(bookId));
        Update update = new Update().pull("comments", Query.query(Criteria.where("$id").is(new ObjectId(commentId))));
        mongoTemplate.updateMulti(query, update, Book.class);
    }

}

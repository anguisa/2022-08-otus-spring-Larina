package ru.otus.service;

import ru.otus.dao.QuestionDao;
import ru.otus.domain.Question;

import java.util.List;

public class QuestionServiceImpl implements QuestionService {

    private final QuestionDao dao;

    public QuestionServiceImpl(QuestionDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Question> findAllQuestions() {
        return dao.findAllQuestions();
    }
}

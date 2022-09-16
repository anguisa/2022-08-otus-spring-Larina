package ru.otus.dao;

import org.springframework.stereotype.Component;
import ru.otus.domain.Question;
import ru.otus.loader.ResourceLoader;

import java.util.List;

@Component
public class QuestionDaoCsv implements QuestionDao {

    private final ResourceLoader<Question> resourceLoader;

    public QuestionDaoCsv(ResourceLoader<Question> resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public List<Question> findAllQuestions() {
        return resourceLoader.loadData();
    }

}

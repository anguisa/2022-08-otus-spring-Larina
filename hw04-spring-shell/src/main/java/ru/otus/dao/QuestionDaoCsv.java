package ru.otus.dao;

import org.springframework.stereotype.Component;
import ru.otus.domain.Question;
import ru.otus.loader.ResourceLoader;
import ru.otus.parser.EntityParserCsv;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionDaoCsv implements QuestionDao {

    private final ResourceLoader<String[]> resourceLoader;
    private final EntityParserCsv<Question> questionParser;

    public QuestionDaoCsv(ResourceLoader<String[]> resourceLoader,
                          EntityParserCsv<Question> questionParser) {
        this.resourceLoader = resourceLoader;
        this.questionParser = questionParser;
    }

    @Override
    public List<Question> findAllQuestions() {
        List<String[]> rawData = resourceLoader.loadData();
        List<Question> questions = new ArrayList<>();
        if (rawData != null) {
            for (String[] line : rawData) {
                questions.add(questionParser.parse(line));
            }
        }
        return questions;
    }

}

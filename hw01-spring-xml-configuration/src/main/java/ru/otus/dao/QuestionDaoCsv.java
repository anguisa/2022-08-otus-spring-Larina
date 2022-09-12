package ru.otus.dao;

import ru.otus.domain.Answer;
import ru.otus.domain.Question;
import ru.otus.service.ResourceLoaderService;

import java.util.ArrayList;
import java.util.List;

public class QuestionDaoCsv implements QuestionDao {

    private final String fileName;
    private final char fileDelimiter;
    private final ResourceLoaderService resourceLoader;

    public QuestionDaoCsv(String fileName, char fileDelimiter, ResourceLoaderService resourceLoader) {
        this.fileName = fileName;
        this.fileDelimiter = fileDelimiter;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public List<Question> findAllQuestions() {
        List<Question> questions = new ArrayList<>();
        resourceLoader.loadData(fileName, fileDelimiter, line -> {
            try {
                String questionText = line[0];
                int answersQuantity = Integer.parseInt(line[1]);
                int correctAnswerNum = Integer.parseInt(line[2]);
                List<Answer> answers = new ArrayList<>();
                for (int i = 1; i <= answersQuantity; i++) {
                    answers.add(new Answer(line[2 + i], i == correctAnswerNum));
                }
                return new Question(questionText, answers);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Wrong file format", ex);
            }
        }, questions);
        return questions;
    }

}

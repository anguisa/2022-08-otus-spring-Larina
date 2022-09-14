package ru.otus.service;

import ru.otus.domain.Question;

import java.util.List;

public class TestingServiceImpl implements TestingService {

    private final QuestionService questionService;

    public TestingServiceImpl(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Override
    public void test() {
        List<Question> questions = questionService.findAllQuestions();
        for (int i = 0; i < questions.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, questions.get(i));
            System.out.println("--------------------");
        }
    }
}

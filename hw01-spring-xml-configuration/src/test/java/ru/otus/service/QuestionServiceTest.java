package ru.otus.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.dao.QuestionDao;
import ru.otus.domain.Answer;
import ru.otus.domain.Question;

import java.util.List;

import static org.mockito.Mockito.mock;

public class QuestionServiceTest {

    private QuestionDao questionDao;
    private QuestionService questionService;

    @BeforeEach
    public void beforeEach() {
        questionDao = mock(QuestionDao.class);
        questionService = new QuestionServiceImpl(questionDao);
    }

    @Test
    public void shouldReturnCorrectQuestions() {
        List<Question> questions = List.of(
            new Question("What is your name?", List.of(new Answer("Vasya", true), new Answer("Petya", false))),
            new Question("How old are you?", List.of(new Answer("18", false), new Answer("20", true), new Answer("25", false)))
        );
        Mockito.when(questionDao.findAllQuestions()).thenReturn(questions);
        Assertions.assertIterableEquals(questions, questionService.findAllQuestions(), "Wrong questions");
    }
}

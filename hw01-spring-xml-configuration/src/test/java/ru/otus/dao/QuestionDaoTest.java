package ru.otus.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.otus.domain.Answer;
import ru.otus.domain.Question;
import ru.otus.service.ResourceLoaderService;

import java.util.List;
import java.util.function.Function;

import static org.mockito.Mockito.mock;

public class QuestionDaoTest {

    private ResourceLoaderService resourceLoader;
    private QuestionDao questionDao;

    @BeforeEach
    public void beforeEach() {
        resourceLoader = mock(ResourceLoaderService.class);
        questionDao = new QuestionDaoCsv("test", '.', resourceLoader);
    }

    @Test
    public void shouldParseCorrectQuestions() {
        List<Question> questions = List.of(
            new Question("What is your name?", List.of(new Answer("Vasya", true), new Answer("Petya", false))),
            new Question("How old are you?", List.of(new Answer("18", false), new Answer("20", true), new Answer("25", false)))
        );
        String[] line1 = new String[] { questions.get(0).getText(), "2", "1",
            questions.get(0).getAnswers().get(0).getText(), questions.get(0).getAnswers().get(1).getText() };
        String[] line2 = new String[] { questions.get(1).getText(), "3", "2",
            questions.get(1).getAnswers().get(0).getText(), questions.get(1).getAnswers().get(1).getText(), questions.get(1).getAnswers().get(2).getText() };
        Mockito.doAnswer(invocation -> {
            Function<String[], Question> parseLineToObject = invocation.getArgument(2);
            List<Question> parsedData = invocation.getArgument(3);
            parsedData.add(parseLineToObject.apply(line1));
            parsedData.add(parseLineToObject.apply(line2));
            return null;
        }).when(resourceLoader).loadData(Mockito.anyString(), Mockito.anyChar(), Mockito.any(), Mockito.any());
        Assertions.assertIterableEquals(questions, questionDao.findAllQuestions(), "Wrong questions");
    }
}

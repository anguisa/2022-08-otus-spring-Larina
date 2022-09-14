package ru.otus.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.domain.Answer;
import ru.otus.domain.Question;
import ru.otus.loader.ResourceLoader;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuestionDaoTest {

    @Mock
    private ResourceLoader<Question> resourceLoader;

    private QuestionDao questionDao;

    @BeforeEach
    public void setUp() {
        questionDao = new QuestionDaoCsv(resourceLoader);
    }

    @Test
    public void shouldParseCorrectQuestions() {
        List<Question> questions = List.of(
            new Question("What is your name?", List.of(new Answer("Vasya", true), new Answer("Petya", false))),
            new Question("How old are you?", List.of(new Answer("18", false), new Answer("20", true), new Answer("25", false)))
        );
        when(resourceLoader.loadData()).thenReturn(questions);
        assertThat(questionDao.findAllQuestions()).isEqualTo(questions);
    }
}

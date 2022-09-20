package ru.otus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.dao.QuestionDao;
import ru.otus.domain.Answer;
import ru.otus.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class QuestionServiceImplTest {

    @Mock
    private QuestionDao questionDao;

    private QuestionService questionService;

    @BeforeEach
    public void setUp() {
        questionService = new QuestionServiceImpl(questionDao);
    }

    @Test
    public void shouldReturnCorrectQuestions() {
        List<Question> questions = List.of(
            new Question("What is your name?", List.of(new Answer(1, "Vasya", true), new Answer(2, "Petya", false))),
            new Question("How old are you?", List.of(new Answer(1, "18", false), new Answer(2, "20", true), new Answer(3, "25", false)))
        );
        Mockito.when(questionDao.findAllQuestions()).thenReturn(questions);
        assertThat(questionService.findAllQuestions()).isEqualTo(questions);
    }

    @Test
    public void shouldDetermineCorrectAnswers() {
        Question question = new Question("Which animals do you like?", List.of(new Answer(1, "Cats", true),
            new Answer(2, "Dogs", true), new Answer(3, "Spiders", false)));
        assertThat(questionService.isCorrectAnswer(question, 1, 2)).isEqualTo(true);
        assertThat(questionService.isCorrectAnswer(question, 1)).isEqualTo(false);
    }
}

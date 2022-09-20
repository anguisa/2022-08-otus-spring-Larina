package ru.otus.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.config.TestingConfig;
import ru.otus.domain.Answer;
import ru.otus.domain.Question;
import ru.otus.domain.Score;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestingServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private LocalizationService localizationService;

    private QuestionService questionService;

    private TestingServiceImpl testingService;

    @ParameterizedTest(name = "User answer={0}, Score={1}")
    @CsvSource({"1, PASSED", "2, FAILED"})
    public void shouldReturnCorrectScore(int answer, Score score) {
        questionService = mock(QuestionServiceImpl.class);
        testingService = new TestingServiceImpl(questionService, ioService, localizationService, new TestingConfig().setQuestionsCount(3).setPassScore(2));

        List<Question> questions = List.of(
            new Question("What is your name?", List.of(new Answer(1, "Vasya", true), new Answer(2, "Petya", false))),
            new Question("How old are you?", List.of(new Answer(1, "18", true), new Answer(2, "20", false), new Answer(3, "25", false))),
            new Question("Which animals do you like?", List.of(new Answer(1, "Cats", true), new Answer(2, "Spiders", false)))
        );
        when(questionService.findAllQuestions()).thenReturn(questions);
        when(questionService.isCorrectAnswer(any(), any())).thenCallRealMethod();
        when(ioService.readStringWithPrompt(anyString())).thenReturn("TEST");
        when(localizationService.localizeMessage(anyString(), any())).thenReturn("TEST");

        when(ioService.readIntWithPrompt(anyString())).thenReturn(answer);
        assertThat(testingService.test()).isEqualTo(score);
    }
}

package ru.otus.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.domain.Answer;
import ru.otus.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuestionParserCsvTest {

    private QuestionParserCsv questionParserCsv;

    @BeforeEach
    public void setUp() {
        questionParserCsv = mock(QuestionParserCsv.class);
    }

    @Test
    public void shouldParseQuestionCorrect() {
        Question question = new Question("What is your name?", List.of(new Answer("Vasya", true), new Answer("Petya", false)));
        String[] line = new String[] { question.getText(), "2", "1", question.getAnswers().get(0).getText(), question.getAnswers().get(1).getText() };
        when(questionParserCsv.parse(any())).thenReturn(question);
        assertThat(questionParserCsv.parse(line)).isEqualTo(question);
    }
}

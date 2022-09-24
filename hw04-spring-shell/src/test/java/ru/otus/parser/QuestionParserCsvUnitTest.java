package ru.otus.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.domain.Answer;
import ru.otus.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class QuestionParserCsvUnitTest {

    private QuestionParserCsv questionParserCsv;

    @BeforeEach
    public void setUp() {
        questionParserCsv = new QuestionParserCsv();
    }

    @Test
    public void shouldParseQuestionCorrect() {
        Question question = new Question("What is your name?", List.of(new Answer(1, "Vasya", true), new Answer(2, "Petya", false)));
        String[] line = new String[] { question.getText(), "2", "1", question.getAnswers().get(0).getText(), question.getAnswers().get(1).getText() };
        assertThat(questionParserCsv.parse(line)).isEqualTo(question);
    }
}

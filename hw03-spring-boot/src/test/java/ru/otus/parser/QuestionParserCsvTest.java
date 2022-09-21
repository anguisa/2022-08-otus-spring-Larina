package ru.otus.parser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.domain.Answer;
import ru.otus.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class QuestionParserCsvTest {

    @Autowired
    private QuestionParserCsv questionParserCsv;

    @Test
    public void shouldParseQuestionCorrect() {
        Question question = new Question("What is your name?", List.of(new Answer(1, "Vasya", true), new Answer(2, "Petya", false)));
        String[] line = new String[] { question.getText(), "2", "1", question.getAnswers().get(0).getText(), question.getAnswers().get(1).getText() };
        assertThat(questionParserCsv.parse(line)).isEqualTo(question);
    }
}

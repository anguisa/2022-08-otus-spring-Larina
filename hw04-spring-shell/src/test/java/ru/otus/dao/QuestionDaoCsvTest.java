package ru.otus.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.domain.Answer;
import ru.otus.domain.Question;
import ru.otus.loader.ResourceLoader;
import ru.otus.parser.EntityParserCsv;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class QuestionDaoCsvTest {

    @MockBean
    private ResourceLoader<String[]> resourceLoader;

    @Autowired
    private EntityParserCsv<Question> questionParser;

    @Autowired
    private QuestionDao questionDao;

    @Test
    public void shouldParseCorrectQuestions() {
        List<Question> questions = List.of(
            new Question("What is your name?", List.of(new Answer(1, "Vasya", true), new Answer(2, "Petya", false))),
            new Question("How old are you?", List.of(new Answer(1, "18", false), new Answer(2, "20", true), new Answer(3, "25", false)))
        );
        List<String[]> lines = List.of(
            new String[] { questions.get(0).getText(), "2", "1", questions.get(0).getAnswers().get(0).getText(), questions.get(0).getAnswers().get(1).getText() },
            new String[] { questions.get(1).getText(), "3", "2", questions.get(1).getAnswers().get(0).getText(), questions.get(1).getAnswers().get(1).getText(), questions.get(1).getAnswers().get(2).getText() }
        );
        when(resourceLoader.loadData()).thenReturn(lines);
        assertThat(questionDao.findAllQuestions()).isEqualTo(questions);
    }
}

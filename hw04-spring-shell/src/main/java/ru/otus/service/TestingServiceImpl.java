package ru.otus.service;

import org.springframework.stereotype.Service;
import ru.otus.config.TestingConfig;
import ru.otus.domain.Person;
import ru.otus.domain.Question;
import ru.otus.domain.Score;

import java.util.List;

@Service
public class TestingServiceImpl implements TestingService {

    private final QuestionService questionService;
    private final IOService ioService;
    private final LocalizationService localizationService;
    private final TestingConfig testingConfig;

    public TestingServiceImpl(QuestionService questionService,
                              IOService ioService,
                              LocalizationService localizationService,
                              TestingConfig testingConfig) {
        this.questionService = questionService;
        this.ioService = ioService;
        this.localizationService = localizationService;
        this.testingConfig = testingConfig;
    }

    @Override
    public Score test(Person person) {
        List<Question> questions = obtainQuestions();
        int correctAnswers = askQuestions(questions, person);
        Score score = calculateScore(correctAnswers);
        printResult(score, person);
        return score;
    }

    private List<Question> obtainQuestions() {
        return questionService.findAllQuestions();
    }

    private int askQuestions(List<Question> questions, Person person) {
        ioService.outputString(localizationService.localizeMessage("testing.start", person.getFirstName(), person.getLastName()));
        int correctAnswers = 0;
        for (int i = 0; i < Math.min(questions.size(), testingConfig.getQuestionsCount()); i++) {
            Question question = questions.get(i);
            int answer = ioService.readIntWithPrompt(localizationService.localizeMessage("testing.question", i + 1, question));
            boolean isCorrectAnswer = questionService.isCorrectAnswer(question, answer);
            if (isCorrectAnswer) {
                correctAnswers++;
            }
        }
        return correctAnswers;
    }

    private Score calculateScore(int correctAnswers) {
        return correctAnswers >= testingConfig.getPassScore() ? Score.PASSED : Score.FAILED;
    }

    private void printResult(Score score, Person person) {
        ioService.outputString(localizationService.localizeMessage("testing.result", person.getFirstName(), person.getLastName(), score));
    }
}

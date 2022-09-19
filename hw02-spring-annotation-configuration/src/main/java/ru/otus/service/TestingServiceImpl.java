package ru.otus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.domain.Person;
import ru.otus.domain.Question;
import ru.otus.domain.Score;

import java.util.List;

@Service
public class TestingServiceImpl implements TestingService {

    private final QuestionService questionService;
    private final IOService ioService;
    private final int passScore;
    private final int questionsCount;

    public TestingServiceImpl(QuestionService questionService,
                              IOService ioService,
                              @Value("${testing.pass_score}") int passScore,
                              @Value("${testing.questions_count}") int questionsCount) {
        this.questionService = questionService;
        this.ioService = ioService;
        this.passScore = passScore;
        this.questionsCount = questionsCount;
    }

    @Override
    public Score test() {
        Person person = obtainUser();
        List<Question> questions = obtainQuestions();
        int correctAnswers = askQuestions(questions, person);
        Score score = calculateScore(correctAnswers);
        printResult(score, person);
        return score;
    }

    private Person obtainUser() {
        String firstName = ioService.readStringWithPrompt("Enter first name: ");
        String lastName = ioService.readStringWithPrompt("Enter last name: ");
        return new Person(firstName, lastName);
    }

    private List<Question> obtainQuestions() {
        return questionService.findAllQuestions();
    }

    private int askQuestions(List<Question> questions, Person person) {
        ioService.outputString(String.format("%s %s, TEST STARTED!", person.getFirstName(), person.getLastName()));
        int correctAnswers = 0;
        for (int i = 0; i < Math.min(questions.size(), questionsCount); i++) {
            Question question = questions.get(i);
            int answer = ioService.readIntWithPrompt(String.format("Q%d. %s\nEnter your answer: ", i + 1, question));
            boolean isCorrectAnswer = questionService.isCorrectAnswer(question, answer);
            if (isCorrectAnswer) {
                correctAnswers++;
            }
        }
        return correctAnswers;
    }

    private Score calculateScore(int correctAnswers) {
        return correctAnswers >= passScore ? Score.PASSED : Score.FAILED;
    }

    private void printResult(Score score, Person person) {
        ioService.outputString(String.format("%s %s result: %s", person.getFirstName(), person.getLastName(), score));
    }
}

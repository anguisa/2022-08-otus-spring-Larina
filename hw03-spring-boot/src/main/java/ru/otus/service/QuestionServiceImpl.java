package ru.otus.service;

import org.springframework.stereotype.Service;
import ru.otus.dao.QuestionDao;
import ru.otus.domain.Question;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionDao dao;

    public QuestionServiceImpl(QuestionDao dao) {
        this.dao = dao;
    }

    @Override
    public List<Question> findAllQuestions() {
        return dao.findAllQuestions();
    }

    @Override
    public boolean isCorrectAnswer(Question question, int... answerNumbers) {
        Set<Integer> correctAnswers = question.getAnswers().stream().filter(answer -> answer.isCorrect()).map(answer -> answer.getNumber()).collect(Collectors.toSet());
        boolean isCorrect = true;
        for (int answerNumber: answerNumbers) {
            boolean removed = correctAnswers.remove(answerNumber);
            if (!removed) {
                isCorrect = false;
                break;
            }
        }
        if (!correctAnswers.isEmpty()) {
            isCorrect = false;
        }
        return isCorrect;
    }
}

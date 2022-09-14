package ru.otus.parser;

import ru.otus.domain.Answer;
import ru.otus.domain.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionParserCsv implements EntityParserCsv<Question> {
    @Override
    public Question parse(String[] line) {
        try {
            String questionText = line[0];
            int answersQuantity = Integer.parseInt(line[1]);
            int correctAnswerNum = Integer.parseInt(line[2]);
            List<Answer> answers = new ArrayList<>();
            for (int i = 1; i <= answersQuantity; i++) {
                answers.add(new Answer(line[2 + i], i == correctAnswerNum));
            }
            return new Question(questionText, answers);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Wrong line format", ex);
        }
    }
}

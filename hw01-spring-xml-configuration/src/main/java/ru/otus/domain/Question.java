package ru.otus.domain;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Question {
    private final String text;
    private final List<Answer> answers;

    public Question(String text, List<Answer> answers) {
        this.text = text;
        this.answers = answers;
    }

    public String getText() {
        return text;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(text, question.text) && Objects.equals(answers, question.answers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, answers);
    }

    @Override
    public String toString() {
        return text + "\n" + answers.stream().map(Object::toString).collect(Collectors.joining("\n"));
    }
}

package ru.otus.domain;

import java.util.Objects;

public class Answer {
    private final int number;
    private final String text;
    private final boolean isCorrect;

    public Answer(int number, String text, boolean isCorrect) {
        this.number = number;
        this.text = text;
        this.isCorrect = isCorrect;
    }

    public int getNumber() {
        return number;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return number == answer.number && isCorrect == answer.isCorrect && Objects.equals(text, answer.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, text, isCorrect);
    }

    @Override
    public String toString() {
        return number + ". " + text;
    }
}

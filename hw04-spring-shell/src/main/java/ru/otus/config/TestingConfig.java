package ru.otus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "application.testing")
@Configuration
public class TestingConfig {

    private int passScore;
    private int questionsCount;

    public int getPassScore() {
        return passScore;
    }

    public int getQuestionsCount() {
        return questionsCount;
    }

    public TestingConfig setPassScore(int passScore) {
        this.passScore = passScore;
        return this;
    }

    public TestingConfig setQuestionsCount(int questionsCount) {
        this.questionsCount = questionsCount;
        return this;
    }
}

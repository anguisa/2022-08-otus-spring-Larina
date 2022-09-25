package ru.otus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "application.loader.csv")
@Configuration
public class LoaderCsvConfig {

    private String fileName;
    private char fileDelimiter;

    public String getFileName() {
        return fileName;
    }

    public char getFileDelimiter() {
        return fileDelimiter;
    }

    public LoaderCsvConfig setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public LoaderCsvConfig setFileDelimiter(char fileDelimiter) {
        this.fileDelimiter = fileDelimiter;
        return this;
    }
}

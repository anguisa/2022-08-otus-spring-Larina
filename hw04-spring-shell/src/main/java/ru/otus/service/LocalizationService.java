package ru.otus.service;

public interface LocalizationService {

    String localizeMessage(String code, Object... args);
}

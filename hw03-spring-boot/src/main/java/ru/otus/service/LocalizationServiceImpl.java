package ru.otus.service;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import ru.otus.config.LocaleConfig;

import java.util.Locale;

@Service
public class LocalizationServiceImpl implements LocalizationService {

    private final MessageSource messageSource;
    private final Locale locale;

    public LocalizationServiceImpl(MessageSource messageSource,
                                   LocaleConfig localeConfig) {
        this.messageSource = messageSource;
        this.locale = localeConfig.getLocale();
    }

    @Override
    public String localizeMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }
}

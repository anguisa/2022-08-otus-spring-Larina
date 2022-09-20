package ru.otus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@ConfigurationProperties(prefix = "application.message")
@Configuration
public class LocaleConfig {

    private Locale locale;

    public Locale getLocale() {
        return locale;
    }

    public LocaleConfig setLocale(Locale locale) {
        this.locale = locale;
        return this;
    }
}

package ru.otus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.otus.dto.formatter.AuthorFormatter;
import ru.otus.dto.formatter.BookShortFormatter;
import ru.otus.dto.formatter.GenreFormatter;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    // для преобразования между объектами и текстом
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new GenreFormatter());
        registry.addFormatter(new AuthorFormatter());
        registry.addFormatter(new BookShortFormatter());
    }
}

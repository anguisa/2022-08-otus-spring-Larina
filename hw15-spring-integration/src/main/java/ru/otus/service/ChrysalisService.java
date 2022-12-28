package ru.otus.service;

import ru.otus.domain.Butterfly;
import ru.otus.domain.Chrysalis;

public interface ChrysalisService {

    Butterfly transform(Chrysalis chrysalis);
}

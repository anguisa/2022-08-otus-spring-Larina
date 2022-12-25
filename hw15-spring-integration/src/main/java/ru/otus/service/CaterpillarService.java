package ru.otus.service;

import ru.otus.domain.Caterpillar;
import ru.otus.domain.Chrysalis;

public interface CaterpillarService {

    Chrysalis pupate(Caterpillar caterpillar);
}

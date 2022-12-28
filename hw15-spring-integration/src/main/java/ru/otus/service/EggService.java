package ru.otus.service;

import ru.otus.domain.Caterpillar;
import ru.otus.domain.Egg;

public interface EggService {

    Caterpillar hatch(Egg egg);
}

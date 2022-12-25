package ru.otus.service;

import org.springframework.stereotype.Service;
import ru.otus.domain.ButterflyType;
import ru.otus.domain.Egg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.concurrent.ThreadLocalRandom.current;

@Service
public class EggGeneratorImpl implements EggGenerator {
    @Override
    public List<Egg> generate() {
        List<Egg> items = new ArrayList<>();
        for (int i = 0; i < current().nextInt(1, 10); i++) {
            items.add(generateEgg());
        }
        return items;
    }

    private Egg generateEgg() {
        return new Egg(UUID.randomUUID(), ButterflyType.values()[current().nextInt(0, ButterflyType.values().length)], 0, 0);
    }
}

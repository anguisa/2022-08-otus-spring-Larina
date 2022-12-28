package ru.otus.service;

import org.springframework.stereotype.Service;
import ru.otus.domain.Caterpillar;
import ru.otus.domain.Egg;

import static java.util.concurrent.ThreadLocalRandom.current;

@Service
public class EggServiceImpl implements EggService {
    @Override
    public Caterpillar hatch(Egg egg) {
        System.out.printf(">>>>>>>> Start hatching from egg %s......\n", egg);
        for (int i = 1; i < current().nextInt(1, 10); i++) {
            egg.setSize(egg.getSize() + 2);
            egg.setAge(egg.getAge() + 1);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Caterpillar caterpillar = new Caterpillar(egg.getId(), egg.getButterflyType(), egg.getAge(), egg.getSize());
        System.out.printf(">>>>>>>> Ready caterpillar: %s\n", caterpillar);
        return caterpillar;
    }
}

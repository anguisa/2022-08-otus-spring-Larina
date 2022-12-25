package ru.otus.service;

import org.springframework.stereotype.Service;
import ru.otus.domain.Caterpillar;
import ru.otus.domain.Chrysalis;

import static java.util.concurrent.ThreadLocalRandom.current;

@Service
public class CaterpillarServiceImpl implements CaterpillarService {
    @Override
    public Chrysalis pupate(Caterpillar caterpillar) {
        System.out.printf(">>>>>>>> Start pupating %s......\n", caterpillar);
        for (int i = 1; i < current().nextInt(1, 10); i++) {
            caterpillar.setSize(caterpillar.getSize() + 2);
            caterpillar.setAge(caterpillar.getAge() + 1);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Chrysalis chrysalis = new Chrysalis(caterpillar.getId(), caterpillar.getButterflyType(), caterpillar.getAge(), caterpillar.getSize());
        System.out.printf(">>>>>>>> Ready chrysalis: %s\n", chrysalis);
        return chrysalis;
    }
}

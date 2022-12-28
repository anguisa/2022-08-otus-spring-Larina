package ru.otus.service;

import org.springframework.stereotype.Service;
import ru.otus.domain.Butterfly;
import ru.otus.domain.Chrysalis;

import static java.util.concurrent.ThreadLocalRandom.current;

@Service
public class ChrysalisServiceImpl implements ChrysalisService {
    @Override
    public Butterfly transform(Chrysalis chrysalis) {
        System.out.printf(">>>>>>>> Start transforming from chrysalis %s......\n", chrysalis);
        for (int i = 1; i < current().nextInt(1, 40); i++) {
            chrysalis.setSize(chrysalis.getSize() - 1);
            chrysalis.setAge(chrysalis.getAge() + 1);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        Butterfly butterfly = new Butterfly(chrysalis.getId(), chrysalis.getButterflyType(), chrysalis.getAge(), chrysalis.getSize());
        System.out.printf(">>>>>>>> Ready butterfly: %s\n", butterfly);
        return butterfly;
    }
}

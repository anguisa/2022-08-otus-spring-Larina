package ru.otus.service;

import org.springframework.stereotype.Service;
import ru.otus.domain.ButterflyEntity;
import ru.otus.domain.Egg;
import ru.otus.integration.ButterflyGateway;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Service
public class ButterflyServiceImpl implements ButterflyService {

    private final ButterflyGateway butterflyGateway;
    private final EggGenerator eggGenerator;

    public ButterflyServiceImpl(ButterflyGateway butterflyGateway, EggGenerator eggGenerator) {
        this.butterflyGateway = butterflyGateway;
        this.eggGenerator = eggGenerator;
    }

    @Override
    public void raise() {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        for (int i = 1; i <= 100; i++) {
            pool.execute(() -> {
                List<Egg> eggs = eggGenerator.generate();
                System.out.printf(">>>>>>>> New eggs: %s\n", eggs.stream().map(Egg::toString).collect(Collectors.toList()));
                List<ButterflyEntity> butterflies = butterflyGateway.transform(eggs);
                System.out.printf(">>>>>>>> Received butterfly entities: %s\n", butterflies.stream().map(ButterflyEntity::toString).collect(Collectors.toList()));
            });
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

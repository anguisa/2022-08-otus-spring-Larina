package ru.otus.service;

import org.springframework.stereotype.Service;
import ru.otus.domain.ButterflyEntity;

@Service
public class BirdServiceImpl implements BirdService {
    @Override
    public void eat(ButterflyEntity butterflyEntity) {
        System.out.println(">>>>>>>> Bird eats: " + butterflyEntity);
    }
}

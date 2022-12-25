package ru.otus.domain;

import java.util.UUID;

public class Egg extends ButterflyEntity {
    public Egg(UUID id, ButterflyType butterflyType, int age, int size) {
        super(id, butterflyType, age, size);
    }

    @Override
    public String toString() {
        return "Egg{" +
            "id=" + getId() +
            ", butterflyType=" + getButterflyType().getTitle() +
            ", age=" + getAge() +
            ", size=" + getSize() +
            ", alive=" + isAlive() +
            '}';
    }
}

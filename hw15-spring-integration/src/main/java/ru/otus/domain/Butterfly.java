package ru.otus.domain;

import java.util.UUID;

public class Butterfly extends ButterflyEntity {
    public Butterfly(UUID id, ButterflyType butterflyType, int age, int size) {
        super(id, butterflyType, age, size);
    }

    @Override
    public String toString() {
        return "Butterfly{" +
            "id=" + getId() +
            ", butterflyType=" + getButterflyType().getTitle() +
            ", age=" + getAge() +
            ", size=" + getSize() +
            ", alive=" + isAlive() +
            '}';
    }
}

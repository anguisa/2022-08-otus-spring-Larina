package ru.otus.domain;

import java.util.UUID;

public class Caterpillar extends ButterflyEntity {
    public Caterpillar(UUID id, ButterflyType butterflyType, int age, int size) {
        super(id, butterflyType, age, size);
    }

    @Override
    public String toString() {
        return "Caterpillar{" +
            "id=" + getId() +
            ", butterflyType=" + getButterflyType().getTitle() +
            ", age=" + getAge() +
            ", size=" + getSize() +
            ", alive=" + isAlive() +
            '}';
    }
}

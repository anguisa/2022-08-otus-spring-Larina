package ru.otus.domain;

import java.util.UUID;

public class Chrysalis extends ButterflyEntity {
    public Chrysalis(UUID id, ButterflyType butterflyType, int age, int size) {
        super(id, butterflyType, age, size);
    }

    @Override
    public String toString() {
        return "Chrysalis{" +
            "id=" + getId() +
            ", butterflyType=" + getButterflyType().getTitle() +
            ", age=" + getAge() +
            ", size=" + getSize() +
            ", alive=" + isAlive() +
            '}';
    }
}

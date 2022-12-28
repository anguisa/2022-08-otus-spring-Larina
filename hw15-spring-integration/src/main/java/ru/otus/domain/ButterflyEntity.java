package ru.otus.domain;

import java.util.UUID;

public abstract class ButterflyEntity {
    private final UUID id;
    private final ButterflyType butterflyType;
    private int age;
    private int size;
    private boolean isAlive;

    public ButterflyEntity(UUID id, ButterflyType butterflyType, int age, int size) {
        this.id = id;
        this.butterflyType = butterflyType;
        this.age = age;
        this.size = size;
        this.isAlive = true;
    }

    public UUID getId() {
        return id;
    }

    public ButterflyType getButterflyType() {
        return butterflyType;
    }

    public int getAge() {
        return age;
    }

    public int getSize() {
        return size;
    }

    public ButterflyEntity setAge(int age) {
        this.age = age;
        return this;
    }

    public ButterflyEntity setSize(int size) {
        this.size = size;
        return this;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public ButterflyEntity setAlive(boolean alive) {
        isAlive = alive;
        return this;
    }
}

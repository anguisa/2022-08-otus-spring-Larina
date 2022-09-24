package ru.otus.loader;

import java.util.List;

public interface ResourceLoader<T> {

    List<T> loadData();
}

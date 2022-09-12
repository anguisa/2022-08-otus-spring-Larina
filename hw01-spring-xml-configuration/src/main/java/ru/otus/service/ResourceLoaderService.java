package ru.otus.service;

import java.util.List;
import java.util.function.Function;

public interface ResourceLoaderService {

    <T> void loadData(String fileName, char fileDelimiter, Function<String[], T> parseLineToObject, List<T> parsedData) ;
}

package ru.otus.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Function;

public class ResourceLoaderServiceImpl implements ResourceLoaderService {
    @Override
    public <T> void loadData(String fileName, char fileDelimiter, Function<String[], T> parseLineToObject, List<T> parsedData) {
        CSVParser parser = new CSVParserBuilder().withSeparator(fileDelimiter).build();
        String[] line;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReaderBuilder(inputStreamReader).withCSVParser(parser).build();
        ) {
            while ((line = csvReader.readNext()) != null) {
                parsedData.add(parseLineToObject.apply(line));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

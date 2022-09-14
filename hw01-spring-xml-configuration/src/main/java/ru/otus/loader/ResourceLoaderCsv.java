package ru.otus.loader;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import ru.otus.parser.EntityParserCsv;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResourceLoaderCsv<T> implements ResourceLoader<T> {

    private final String fileName;
    private final char fileDelimiter;
    private final EntityParserCsv<T> entityParser;

    public ResourceLoaderCsv(String fileName, char fileDelimiter, EntityParserCsv<T> entityParser) {
        this.fileName = fileName;
        this.fileDelimiter = fileDelimiter;
        this.entityParser = entityParser;
    }

    @Override
    public List<T> loadData() {
        CSVParser parser = new CSVParserBuilder().withSeparator(fileDelimiter).build();
        String[] line;
        List<T> parsedData = new ArrayList<>();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReaderBuilder(inputStreamReader).withCSVParser(parser).build();
        ) {
            while ((line = csvReader.readNext()) != null) {
                parsedData.add(entityParser.parse(line));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return parsedData;
    }
}

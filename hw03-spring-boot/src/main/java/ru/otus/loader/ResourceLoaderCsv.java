package ru.otus.loader;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.stereotype.Component;
import ru.otus.config.LoaderCsvConfig;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class ResourceLoaderCsv implements ResourceLoader<String[]> {

    private final LoaderCsvConfig loaderCsvConfig;

    public ResourceLoaderCsv(LoaderCsvConfig loaderCsvConfig) {
        this.loaderCsvConfig = loaderCsvConfig;
    }

    @Override
    public List<String[]> loadData() {
        CSVParser parser = new CSVParserBuilder().withSeparator(loaderCsvConfig.getFileDelimiter()).build();
        List<String[]> rawData;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(loaderCsvConfig.getFileName());
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReaderBuilder(inputStreamReader).withCSVParser(parser).build();
        ) {
            rawData = csvReader.readAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return rawData;
    }
}

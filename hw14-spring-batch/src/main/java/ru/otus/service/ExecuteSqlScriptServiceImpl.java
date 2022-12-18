package ru.otus.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class ExecuteSqlScriptServiceImpl implements ExecuteSqlScriptService {

    private final DataSource dataSource;

    public ExecuteSqlScriptServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void executeSqlScript(String fileName) {
        Resource resource = new ClassPathResource(fileName);
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(resource);
        databasePopulator.execute(dataSource);
    }
}

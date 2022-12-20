package ru.otus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "app")
@Configuration
public class AppConfig {

    private String initSchemaFileName;
    private String updateSchemaFileName;

    public String getInitSchemaFileName() {
        return initSchemaFileName;
    }

    public AppConfig setInitSchemaFileName(String initSchemaFileName) {
        this.initSchemaFileName = initSchemaFileName;
        return this;
    }

    public String getUpdateSchemaFileName() {
        return updateSchemaFileName;
    }

    public AppConfig setUpdateSchemaFileName(String updateSchemaFileName) {
        this.updateSchemaFileName = updateSchemaFileName;
        return this;
    }
}

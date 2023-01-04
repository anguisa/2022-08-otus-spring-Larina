package ru.otus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        // http://localhost:8080
        // http://localhost:8080/actuator
        // http://localhost:8080/actuator/health
        // http://localhost:8080/actuator/logfile
        // http://localhost:8080/data-rest
        // http://localhost:8080/data-rest/explorer/index.html - HAL explorer
    }
}

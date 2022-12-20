package ru.otus;

import com.github.cloudyrock.spring.v5.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableMongock
@SpringBootApplication
@EnableConfigurationProperties
public class Main {

    // docker-compose -f mongo-stack.yaml up -d
    // http://localhost:8081/ - mongo
    // http://localhost:8082/ - h2
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}

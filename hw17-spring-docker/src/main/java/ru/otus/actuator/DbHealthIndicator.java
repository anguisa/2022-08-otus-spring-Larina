package ru.otus.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DbHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    public DbHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            jdbcTemplate.execute("select 1");
            return Health.up()
                .withDetail("message", "DB is up")
                .build();
        } catch (Throwable t) {
            return Health.down()
                .withDetail("message", "DB is down")
                .build();
        }
    }
}

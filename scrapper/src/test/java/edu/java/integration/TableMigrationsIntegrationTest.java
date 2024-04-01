package edu.java.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(classes = {TestConfig.class})
@Testcontainers
public class TableMigrationsIntegrationTest extends IntegrationTest {
    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void updateTable() {
        jdbcTemplate.update("INSERT INTO scrapper.public.chat (id, created_at) VALUES (100000, CURRENT_TIMESTAMP)");
    }
}

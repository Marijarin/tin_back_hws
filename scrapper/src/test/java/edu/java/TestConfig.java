package edu.java;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import static edu.java.IntegrationTest.POSTGRES;

@Configuration
public class TestConfig {
    @Bean
    @Primary
    public DataSource dataSource() {
       return DataSourceBuilder.create()
           .url(POSTGRES.getJdbcUrl())
           .username(POSTGRES.getUsername())
           .password(POSTGRES.getPassword())
           .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}

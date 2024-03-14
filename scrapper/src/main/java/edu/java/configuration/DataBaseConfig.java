package edu.java.configuration;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataBaseConfig {
    private final ApplicationConfig applicationConfig;

    public DataBaseConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create()
            .url(applicationConfig.url())
            .username(applicationConfig.userName())
            .password(applicationConfig.password())
            .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}

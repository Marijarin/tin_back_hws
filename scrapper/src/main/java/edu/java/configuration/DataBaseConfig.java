package edu.java.configuration;

import javax.sql.DataSource;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "spring.datasource", ignoreUnknownFields = false)
public record DataBaseConfig (
    @NotNull
    String url,
    @NotNull
    String username,
    @NotNull
    String password,
    @NotNull
    String driverClassName
){}

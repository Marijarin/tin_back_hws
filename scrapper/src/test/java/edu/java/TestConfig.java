package edu.java;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import static edu.java.IntegrationTest.POSTGRES;

@Configuration
@EnableTransactionManagement
public class TestConfig {
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
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

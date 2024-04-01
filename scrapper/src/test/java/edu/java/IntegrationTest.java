package edu.java;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.resource.SearchPathResourceAccessor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
public class IntegrationTest {
    public static PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres");
        POSTGRES.start();

        try {
            runMigrations(POSTGRES);
        } catch (LiquibaseException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void runMigrations(JdbcDatabaseContainer<?> c) throws LiquibaseException, SQLException {
        java.sql.Connection connection = DriverManager
            .getConnection(c.getJdbcUrl(), c.getUsername(), c.getPassword());
        Database database = DatabaseFactory
            .getInstance()
            .findCorrectDatabaseImplementation(new JdbcConnection(connection));
        var changelogPath = new File(".").toPath().toAbsolutePath()
            .getParent()
            .getParent()
            .resolve("migrations");

        try (ResourceAccessor resourceAccessor = new SearchPathResourceAccessor(
            new DirectoryResourceAccessor(changelogPath)
        )) {
            Liquibase liquibase = new liquibase
                .Liquibase("master.yaml", resourceAccessor, database);

            liquibase.update(new Contexts(), new LabelExpression());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}

package edu.java;

import java.io.File;
import java.io.StringWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.resource.SearchPathResourceAccessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {IntegrationTest.class})
@TestPropertySource("/application-test.yml")
//@EnableJdbcRepositories(basePackages = "edu.java.dao")
@Testcontainers
public class IntegrationTest {
    public static PostgreSQLContainer<?> POSTGRES;
    //@Autowired ChatRepository chatRepository;

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
            .getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
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
                .Liquibase("master.xml", resourceAccessor, database);
            var writer = new StringWriter();
            liquibase.update(new Contexts(), writer);
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

//    @Test
//    public void performSql() throws SQLException {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(POSTGRES.getDriverClassName());
//        dataSource.setUrl(POSTGRES.getJdbcUrl());
//        dataSource.setUsername(POSTGRES.getUsername());
//        dataSource.setPassword(POSTGRES.getPassword());
//        JdbcTemplate jdbcTemplate = new JdbcTemplate();
//        jdbcTemplate.setDataSource(dataSource);
//        jdbcTemplate.execute("DROP TABLE scrapper.information_schema.sql_features");
//
//
//    }
//    @Test
//    public void addsChat() {
//        Chat result = chatRepository
//            .save(new Chat(1L, OffsetDateTime.now(), new Link(1L, "url", "desc")));
//        assertEquals(1L, result.getId());
//        Optional<Chat> saved = chatRepository.findById(1L);
//        assertTrue(saved.isPresent());
//        saved.ifPresent(s -> assertEquals(s, result));
//    }

    @Test
    public void runsDB() {
        var result2 = POSTGRES.withDatabaseName("scrapper.public.chat").getEnv();

        assertTrue(POSTGRES.withDatabaseName("scrapper.public.chat").isRunning());
        assertTrue(result2.toString()
            .contains("POSTGRES_USER=postgres, POSTGRES_PASSWORD=postgres, POSTGRES_DB=scrapper"));
    }

    @Test
    public void createdDb() {
        assertTrue(POSTGRES.withDatabaseName("scrapper.public.chat").isCreated());
        assertTrue(POSTGRES.withDatabaseName("scrapper.public.chat").isRunning());
    }
}

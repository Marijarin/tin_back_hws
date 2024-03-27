package edu.java;

import edu.java.domain.jdbc.JdbcLinkDao;
import edu.java.service.LinkService;
import edu.java.service.jdbc.JdbcLinkService;
import java.net.URI;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Sql(value = "classpath:sql/put_chat.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class JdbcLinkServiceTest extends IntegrationTest {
    private final JdbcLinkService linkService;

    @Autowired
    public JdbcLinkServiceTest(JdbcLinkService linkService) {
        this.linkService = linkService;
    }

    @Test
    @Transactional
    @Rollback
    void addsAndFindsLink() {
        var chatId = 10000L;
        var uri = URI.create(
            "https://stackoverflow.com/questions/68547843/bean-named-userservice-is-expected-to-be-of-type-demo-spring-orm-userservice");

        var result = linkService.add(chatId, uri);
        var listResult = linkService.listAll(chatId);

        assertThat(result.getUri()).isEqualTo(uri);
        assertThat(listResult.stream().toList().getFirst().getUri()).isEqualTo(uri);
    }

    @Test
    @Transactional
    @Rollback
    void removesAndNotFindsLink() {
        var chatId = 10000L;
        var uri = URI.create(
            "https://stackoverflow.com/questions/68547843/bean-named-userservice-is-expected-to-be-of-type-demo-spring-orm-userservice");
        linkService.add(chatId, uri);
        linkService.remove(chatId, uri);

        var listResult = linkService.listAll(chatId);

        assertThat(listResult.isEmpty()).isTrue();
    }

    @Configuration
    @ComponentScan("edu.java.domain.jdbc")
    @EnableTransactionManagement
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class JdbcTestConfiguration {
        @Bean
        public PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
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

        @Bean
        public LinkService linkService(
            JdbcLinkDao linkDao
        ) {
            return new JdbcLinkService(linkDao);
        }
    }

}

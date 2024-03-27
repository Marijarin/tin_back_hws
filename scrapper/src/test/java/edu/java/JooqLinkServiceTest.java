package edu.java;

import edu.java.service.jooq.JooqLinkService;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {JooqTestConfig.class, JooqAutoConfiguration.class})
@Sql(value = "classpath:sql/put_chat.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class JooqLinkServiceTest extends IntegrationTest {
    private final JooqLinkService linkService;

    @Autowired
    public JooqLinkServiceTest(JooqLinkService linkService) {
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

}
